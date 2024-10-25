package com.carkzis.ananke.data

import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.data.model.NewCharacter
import com.carkzis.ananke.data.repository.DefaultYouRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.testdoubles.ControllableYouDao
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.RandomUserNameGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class YouRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var youDao: YouDao
    private lateinit var youRepository: YouRepository

    @Before
    fun setUp() {
        youDao = ControllableYouDao()
        youRepository = DefaultYouRepository(youDao)
    }

    @Test
    fun `repository adds new character with randomised name`() = runTest {
        val newCharacter = NewCharacter(123L, 456L)
        val initialCharacterId = 0L
        youRepository.addNewCharacter(newCharacter)

        val initialCharacterName = youRepository
            .getCharacterForUser(initialCharacterId)
            .first()
            .character

        val (characterNameAdjective, characterNameAnimal, characterNameNumber) = initialCharacterName
            .split("-", limit = 3)

        assertTrue(RandomUserNameGenerator.randomCharacterAdjectives.contains(characterNameAdjective))
        assertTrue(RandomUserNameGenerator.randomCharacterAnimals.contains(characterNameAnimal))
        assertTrue(characterNameNumber.toInt() in 10_000 until 100_000)
    }

    @Test
    fun `repository retrieves a character with username`() = runTest {
        val newCharacter = NewCharacter(123L, 456L)
        youRepository.addNewCharacter(newCharacter)

        val retrievedCharacter = youRepository.getCharacterForUser(newCharacter.userId)

        // TODO: Complete.
    }

    @Test
    fun `repository adds new character to a particular game`() = runTest {

    }

    @Test
    fun `repository does not add duplicate name within a game`() = runTest {

    }

    @Test
    fun `repository does not add new character if character already exists in game`() = runTest {

    }

    @Test
    fun `repository does not add new character if user already exists in game`() = runTest {

    }

    @Test
    fun `repository retrieves a particular character for current game`() = runTest {

    }

    @Test
    fun `repository updates particular character for current game`() = runTest {

    }
}