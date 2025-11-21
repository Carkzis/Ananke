package com.carkzis.ananke.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.carkzis.ananke.data.database.AnankeDataStore
import com.carkzis.ananke.data.database.DefaultAnankeDataStore
import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.data.repository.DefaultYouRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.testdoubles.ControllableYouDao
import com.carkzis.ananke.testdoubles.DuplicatingCharacterNameGenerator
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.you.CharacterAlreadyExistsForUserException
import com.carkzis.ananke.ui.screens.you.CharacterDoesNotExistException
import com.carkzis.ananke.ui.screens.you.CharacterNameTakenException
import com.carkzis.ananke.ui.screens.you.CharacterNamingException
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.assertNameHasExpectedFormat
import com.carkzis.ananke.utils.assertUserHasExpectedFormat
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder


@OptIn(ExperimentalCoroutinesApi::class)
class YouRepositoryTest {
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private lateinit var youDao: ControllableYouDao
    private lateinit var youRepository: YouRepository
    private lateinit var anankeDataStore: AnankeDataStore

    @Before
    fun setUp() {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_data_store.preferences_pb") }
        )
        anankeDataStore = DefaultAnankeDataStore(testDataStore)

        youDao = ControllableYouDao()
        youRepository = DefaultYouRepository(
            youDao = youDao,
            anankeDataStore = anankeDataStore
        )
    }

    @Test
    fun `repository adds new character with randomised name`() = runTest {
        val newCharacter = NewCharacter(123L, 456L)
        youRepository.addNewCharacter(newCharacter)

        val initialCharacterName = youDao.characters.value.first().characterName

        assertNameHasExpectedFormat(initialCharacterName)
    }

    @Test
    fun `repository retrieves a character with username`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val newCharacter = NewCharacter(userForCharacter.userId, dummyGameEntities.first().gameId)
        youRepository.addNewCharacter(newCharacter)

        val retrievedCharacter = youRepository.getCharacterForUser(
            userForCharacter.toDomain(), dummyGameEntities.first().gameId
        ).first()

        assertEquals(dummyUserEntities.first().username, retrievedCharacter.userName)
    }

    @Test
    fun `repository adds and retrieves new character to a particular game`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val currentGameForUser = dummyGameEntities.first()
        val otherGameForUser = dummyGameEntities.last()

        val newCharacterForCurrentGame = NewCharacter(userForCharacter.userId, currentGameForUser.gameId)
        val newCharacterForOtherGame = NewCharacter(userForCharacter.userId, otherGameForUser.gameId)

        youRepository.addNewCharacter(newCharacterForCurrentGame)
        youRepository.addNewCharacter(newCharacterForOtherGame)

        val retrievedCharacter = youRepository.getCharacterForUser(
            userForCharacter.toDomain(), otherGameForUser.gameId
        ).first()

        assertTrue(youDao.characters.value.size == 2)
        assertTrue(youDao.characters.value.map { it.gameOwnerId }.contains(otherGameForUser.gameId))
        assertTrue(youDao.characters.value.map { it.characterId }.contains(retrievedCharacter.id.toLong()))
    }

    @Test(expected = CharacterNamingException::class)
    fun `repository does not add duplicate name within a game for new character`() = runTest {
        youRepository = DefaultYouRepository(youDao, DuplicatingCharacterNameGenerator)

        val userOne = dummyUserEntities.first()
        val userTwo = dummyUserEntities.last()

        youRepository.addNewCharacter(NewCharacter(userOne.userId, dummyGameEntities.first().gameId))
        youRepository.addNewCharacter(NewCharacter(userTwo.userId, dummyGameEntities.first().gameId))
    }

    @Test(expected = CharacterAlreadyExistsForUserException::class)
    fun `repository does not add new character if user already exists in game`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val newCharacter = NewCharacter(userForCharacter.userId, dummyGameEntities.first().gameId)

        youRepository.addNewCharacter(newCharacter)
        youRepository.addNewCharacter(newCharacter)

        assertEquals(1, youDao.characters.value.size)
    }

    @Test
    fun `repository updates particular character for current game`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val currentGameIdForUser = dummyGameEntities.first().gameId
        val newCharacterForCurrentGame = NewCharacter(userForCharacter.userId, currentGameIdForUser)

        youRepository.addNewCharacter(newCharacterForCurrentGame)

        val currentCharacter = youRepository.getCharacterForUser(
            userForCharacter.toDomain(), currentGameIdForUser
        ).first()

        val expectedUpdatedCharacter = currentCharacter.copy(
            character = "New Name"
        )

        youRepository.updateCharacter(expectedUpdatedCharacter, currentGameIdForUser, currentCharacter)

        val retrievedCharacter = youRepository.getCharacterForUser(
            userForCharacter.toDomain(), currentGameIdForUser
        ).first()

        assertEquals(expectedUpdatedCharacter, retrievedCharacter)
    }

    @Test(expected = CharacterDoesNotExistException::class)
    fun `repository does not update character if does not exist`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val currentGameIdForUser = dummyGameEntities.first().gameId
        val newCharacterForCurrentGame = NewCharacter(userForCharacter.userId, currentGameIdForUser)
        val nonExistentGameId = -1L

        youRepository.addNewCharacter(newCharacterForCurrentGame)

        val currentCharacter = youRepository.getCharacterForUser(
            userForCharacter.toDomain(), currentGameIdForUser
        ).first()

        val nonExistentCharacter = currentCharacter.copy(
            id = nonExistentGameId.toString()
        )

        youRepository.updateCharacter(nonExistentCharacter, currentGameIdForUser, currentCharacter)
    }

    @Test(expected = CharacterNameTakenException::class)
    fun `repository does not update character with a duplicate name within a game`() = runTest {
        val firstUserForCharacter = dummyUserEntities.first()
        val secondUserForCharacter = dummyUserEntities.last()
        val currentGameIdForUsers = dummyGameEntities.first().gameId

        val firstNewCharacterForCurrentGame = NewCharacter(firstUserForCharacter.userId, currentGameIdForUsers)
        val secondNewCharacterForCurrentGame = NewCharacter(secondUserForCharacter.userId, currentGameIdForUsers)

        youRepository.addNewCharacter(firstNewCharacterForCurrentGame)
        youRepository.addNewCharacter(secondNewCharacterForCurrentGame)

        val firstCharacterName = youRepository.getCharacterForUser(
            firstUserForCharacter.toDomain(), currentGameIdForUsers
        ).first().character

        val secondCharacterNamedAfterFirstCharacter = youRepository.getCharacterForUser(
            secondUserForCharacter.toDomain(), currentGameIdForUsers
        ).first()
        val secondCharacterNamedAfterFirstCharacterWithNewName = secondCharacterNamedAfterFirstCharacter.copy(
            character = firstCharacterName
        )

        youRepository.updateCharacter(
            secondCharacterNamedAfterFirstCharacterWithNewName,
            currentGameIdForUsers,
            secondCharacterNamedAfterFirstCharacter
        )
    }

    @Test
    fun `repository still updates character if name is the same as their current one`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val currentGameIdForUser = dummyGameEntities.first().gameId
        val newCharacterForCurrentGame = NewCharacter(userForCharacter.userId, currentGameIdForUser)

        youRepository.addNewCharacter(newCharacterForCurrentGame)

        val currentCharacter = youRepository.getCharacterForUser(
            userForCharacter.toDomain(), currentGameIdForUser
        ).first()

        youRepository.updateCharacter(
            character = currentCharacter,
            currentGameId = currentGameIdForUser,
            formerGameCharacter = currentCharacter
        )
    }

    @Test
    fun `repository creates current user if not exist on request and adds to preferences`() = runTest {
        val currentUser = youRepository.getCurrentUser().first()

        assertUserHasExpectedFormat(currentUser.name)
    }

    @Test
    fun `repository returns current user if exist on request`() = runTest {
        val expectedUser = dummyUserEntities.first()
        anankeDataStore.setCurrentUserId(expectedUser.userId.toString())
        youDao.insertUser(expectedUser)

        val actualUser = youRepository.getCurrentUser().first()
        assertEquals(expectedUser.toDomain(), actualUser)
    }

    @Test
    fun `repository deletes all characters for a game`() = runTest {
        val expectedGameId = dummyGameEntities.first().gameId
        val nonDeletedGameId = dummyGameEntities.last().gameId

        val firstTeamMember = dummyUserEntities.first()
        val secondTeamMember = dummyUserEntities.last()

        val newCharacterForCurrentGame = NewCharacter(firstTeamMember.userId, expectedGameId)
        val newCharacterForNonDeletedGame = NewCharacter(secondTeamMember.userId, nonDeletedGameId)

        youRepository.addNewCharacter(newCharacterForCurrentGame)
        youRepository.addNewCharacter(newCharacterForNonDeletedGame)

        youRepository.deleteCharactersForGame(expectedGameId)

        val charactersForDeletedGame = youDao.characters.value.filter { it.gameOwnerId == expectedGameId }
        assertTrue(charactersForDeletedGame.isEmpty())

        val charactersForNonDeletedGame = youDao.characters.value.filter { it.gameOwnerId == nonDeletedGameId }
        assertTrue(charactersForNonDeletedGame.isNotEmpty())
    }
}