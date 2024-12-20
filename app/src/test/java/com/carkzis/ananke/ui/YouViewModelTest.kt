package com.carkzis.ananke.ui

import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.network.userForTesting
import com.carkzis.ananke.data.repository.DefaultYouRepository
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableYouDao
import com.carkzis.ananke.ui.screens.you.YouViewModel
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import com.carkzis.ananke.utils.assertNameHasExpectedFormat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class YouViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: YouViewModel
    private lateinit var gameRepository: ControllableGameRepository
    private lateinit var youRepository: YouRepository
    private lateinit var youDao: ControllableYouDao

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        youDao = ControllableYouDao()
        youRepository = DefaultYouRepository(youDao, RandomCharacterNameGenerator)
        viewModel = YouViewModel(GameStateUseCase(gameRepository), youRepository)
    }

    @Test
    fun `view model displays no game title if unavailable`() = runTest {
        val expectedGameTitle = ""

        var actualGameTitle = "NOT EMPTY"
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentGame.collect {
                actualGameTitle = it.name
            }
        }

        Assert.assertEquals(expectedGameTitle, actualGameTitle)

        collection.cancel()
    }

    @Test
    fun `view model displays game title if available`() = runTest {
        val expectedGameTitle = "A Game"
        val currentGame = CurrentGame("1", expectedGameTitle, "A Description")

        var actualGameTitle = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentGame.collect {
                actualGameTitle = it.name
            }
        }

        gameRepository.emitCurrentGame(currentGame)

        assertEquals(expectedGameTitle, actualGameTitle)

        collection.cancel()
    }

    @Test
    fun `view model adds current user when initialised`() = runTest {
        val expectedUserId = userForTesting.id
        val currentGame = CurrentGame("1", "A Game", "A Description")

        gameRepository.emitCurrentGame(currentGame)

        var charactersForUserId = listOf<CharacterEntity>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.currentGame.collect {
                charactersForUserId = youDao.getCharactersForUserId(expectedUserId).first()
            }
        }

        assertEquals(1, charactersForUserId.size)

        collection.cancel()
    }

    @Test
    fun `view model displays an initial character name`() = runTest {
        val currentGame = CurrentGame("1", "A Game", "A Description")

        gameRepository.emitCurrentGame(currentGame)

        var actualCharacterName = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {
                actualCharacterName = it.character
            }
        }

        assertNameHasExpectedFormat(actualCharacterName)

        collection.cancel()
    }

    @Test
    fun `view model displays an empty initial biography`() = runTest {
        val currentGame = CurrentGame("1", "A Game", "A Description")

        gameRepository.emitCurrentGame(currentGame)

        var actualCharacterBio = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.character.collect {
                actualCharacterBio = it.bio
            }
        }

        assertTrue(actualCharacterBio.isEmpty())

        collection.cancel()
    }
}