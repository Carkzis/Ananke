package com.carkzis.ananke.data

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.NewCharacter
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class YouRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var youDao: ControllableYouDao
    private lateinit var youRepository: YouRepository

    @Before
    fun setUp() {
        youDao = ControllableYouDao()
        youRepository = DefaultYouRepository(youDao)
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

        youRepository.updateCharacter(expectedUpdatedCharacter, currentGameIdForUser)

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

        youRepository.updateCharacter(nonExistentCharacter, currentGameIdForUser)
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
        ).first().copy(
            character = firstCharacterName
        )

        youRepository.updateCharacter(secondCharacterNamedAfterFirstCharacter, currentGameIdForUsers)
    }
}