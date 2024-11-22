package com.carkzis.ananke.data

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.repository.DefaultYouRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.testdoubles.ControllableYouDao
import com.carkzis.ananke.testdoubles.DuplicatingCharacterNameGenerator
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.you.CharacterNamingException
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
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
        val (characterNameAdjective, characterNameAnimal, characterNameNumber) = initialCharacterName
            .split("-", limit = 3)

        assertTrue(RandomCharacterNameGenerator.randomCharacterAdjectives.contains(characterNameAdjective))
        assertTrue(RandomCharacterNameGenerator.randomCharacterAnimals.contains(characterNameAnimal))
        assertTrue(characterNameNumber.toInt() in 10_000 until 100_000)
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
            userForCharacter.toDomain(), currentGameForUser.gameId
        ).first()

        assertTrue(youDao.characterGameCrossReferences.size == 2)
        assertEquals(currentGameForUser.gameId, youDao.characterGameCrossReferences.first().gameId)
        assertEquals(retrievedCharacter.id, youDao.characterGameCrossReferences.first().characterId.toString())
    }

    @Test(expected = CharacterNamingException::class)
    fun `repository does not add duplicate name within a game for new character`() = runTest {
        youRepository = DefaultYouRepository(youDao, DuplicatingCharacterNameGenerator)

        val userOne = dummyUserEntities.first()
        val userTwo = dummyUserEntities.last()

        youRepository.addNewCharacter(NewCharacter(userOne.userId, dummyGameEntities.first().gameId))
        youRepository.addNewCharacter(NewCharacter(userTwo.userId, dummyGameEntities.first().gameId))
    }

    @Test
    fun `repository does not add new character if user already exists in game`() = runTest {
        val userForCharacter = dummyUserEntities.first()
        val newCharacter = NewCharacter(userForCharacter.userId, dummyGameEntities.first().gameId)

        youRepository.addNewCharacter(newCharacter)
        youRepository.addNewCharacter(newCharacter)

        assertEquals(1, youDao.characterGameCrossReferences.size)
    }

    @Test
    fun `repository updates particular character for current game`() = runTest {

    }

    @Test
    fun `repository does update character with a duplicate name within a game`() = runTest {

    }
}