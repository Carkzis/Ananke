package com.carkzis.ananke

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.NewGameScreenMessages
import com.carkzis.ananke.ui.screens.NewGameScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewGameScreenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NewGameScreenViewModel
    private lateinit var gameRepository: ControllableGameRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        viewModel = NewGameScreenViewModel(gameRepository)
    }

    @Test
    fun `view model can add game to repository`() = runTest {
        val games = mutableListOf<Game>()
        val collection = launch(UnconfinedTestDispatcher()) {
            gameRepository.getGames().collect { games.add(it.last()) }
        }

        val newGame = NewGame("aName", "aDescription")
        viewModel.addNewGame(newGame)

        assertTrue(games.contains(newGame.asGame()))

        collection.cancel()
    }

    @Test
    fun `view model holds empty string for game title`() = runTest {
        assertEquals("", viewModel.gameTitle.value)
    }

    @Test
    fun `view model sets new game title`() = runTest {
        val expectedGameTitle = "Super Ananke Bros."
        viewModel.setGameTitle(expectedGameTitle)
        assertEquals(expectedGameTitle, viewModel.gameTitle.value)
    }

    @Test
    fun `view model sends toast message when game title character length exceeded`() = runTest {
        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        val longGameTitle = "LONG".repeat(8)
        viewModel.setGameTitle(longGameTitle)

        assertEquals(NewGameScreenMessages.GAME_TITLE_TOO_LONG.message, message)

        collection.cancel()
    }

    @Test
    fun `view model does not sets new game title if too long`() = runTest {
        val longGameTitle = "LONG".repeat(8)
        viewModel.setGameTitle(longGameTitle)
        assertEquals("", viewModel.gameTitle.value)
    }

    @Test
    fun `view model sends toast message when game title empty`() = runTest {
        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        viewModel.setGameTitle("")

        assertEquals(NewGameScreenMessages.GAME_TITLE_EMPTY.message, message)

        collection.cancel()
    }

    @Test
    fun `view model holds empty string for game description`() = runTest {
        assertEquals("", viewModel.gameDescription.value)
    }

    @Test
    fun `view model sets new game description`() = runTest {
        val expectedGameDescription = "There are things in this game."
        viewModel.setGameDescription(expectedGameDescription)
        assertEquals(expectedGameDescription, viewModel.gameDescription.value)
    }

    @Test
    fun `view model sends toast message when game description character length exceeded`() = runTest {
        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        val longGameDescription = "LONG".repeat(51)
        viewModel.setGameDescription(longGameDescription)

        assertEquals(NewGameScreenMessages.GAME_DESCRIPTION_TOO_LONG.message, message)

        collection.cancel()
    }

    @Test
    fun `view model does not sets new game description if too long`() = runTest {
        val longGameTitle = "LONG".repeat(51)
        viewModel.setGameDescription(longGameTitle)
        assertEquals("", viewModel.gameDescription.value)
    }
}