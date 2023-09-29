package com.carkzis.ananke

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.NewGameMessage
import com.carkzis.ananke.ui.screens.NewGameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NewGameViewModel
    private lateinit var gameRepository: ControllableGameRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        viewModel = NewGameViewModel(gameRepository)
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
    fun `view model sends success event if game added to repository`() = runTest {
        var success = false
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.addGameSuccessEvent.collect { success = it }
        }

        val newGame = NewGame("aName", "aDescription")
        viewModel.addNewGame(newGame)

        assertTrue(success)

        collection.cancel()
    }

    @Test
    fun `view model sends failure event if game cannot be added to repository`() = runTest {
        var success = true
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.addGameSuccessEvent.collect { success = it }
        }

        val invalidNewGame = NewGame("", "")
        viewModel.addNewGame(invalidNewGame)

        assertFalse(success)

        collection.cancel()
    }

    @Test
    fun `view model holds empty string for game title`() = runTest {
        assertEquals("", viewModel.gameTitle)
    }

    @Test
    fun `view model sets new game title`() = runTest {
        val expectedGameTitle = "Super Ananke Bros."
        viewModel.updateGameTitle(expectedGameTitle)
        assertEquals(expectedGameTitle, viewModel.gameTitle)
    }

    @Test
    fun `view model sends toast message when game title character length exceeded`() = runTest {
        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        val longGameTitle = "LONG".repeat(8) // 32 characters
        viewModel.updateGameTitle(longGameTitle)

        assertEquals(NewGameMessage.GAME_TITLE_TOO_LONG.message, message)

        collection.cancel()
    }

    @Test
    fun `view model does not sets new game title if too long`() = runTest {
        val longGameTitle = "LONG".repeat(8) // 32 characters
        viewModel.updateGameTitle(longGameTitle)
        assertEquals("", viewModel.gameTitle)
    }

    @Test
    fun `view model sends toast message about game title only when game title empty and description invalid and game added`() = runTest {
        val messages = mutableListOf<String>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        viewModel.addNewGame(NewGame("", ""))

        assertEquals(NewGameMessage.GAME_TITLE_EMPTY.message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about game title game title too short and game added`() = runTest {
        val messages = mutableListOf<String>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        viewModel.addNewGame(NewGame("Shor", ""))

        assertEquals(NewGameMessage.GAME_TITLE_TOO_SHORT.message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about game description when invalid and game added but title is valid`() = runTest {
        val messages = mutableListOf<String>()
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        val longGameDescription = "LONG".repeat(51) // 204 characters
        viewModel.addNewGame(NewGame("A Game With No Description", longGameDescription))

        assertEquals(NewGameMessage.GAME_DESCRIPTION_TOO_LONG.message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model holds empty string for game description`() = runTest {
        assertEquals("", viewModel.gameDescription)
    }

    @Test
    fun `view model sets new game description`() = runTest {
        val expectedGameDescription = "There are things in this game."
        viewModel.updateGameDescription(expectedGameDescription)
        assertEquals(expectedGameDescription, viewModel.gameDescription)
    }

    @Test
    fun `view model sends toast message when game description character length exceeded`() = runTest {
        var message = ""
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { message = it }
        }

        val longGameDescription = "LONG".repeat(51) // 204 characters
        viewModel.updateGameDescription(longGameDescription)

        assertEquals(NewGameMessage.GAME_DESCRIPTION_TOO_LONG.message, message)

        collection.cancel()
    }

    @Test
    fun `view model does not sets new game description if too long`() = runTest {
        val longGameTitle = "LONG".repeat(51) // 204 characters
        viewModel.updateGameDescription(longGameTitle)
        assertEquals("", viewModel.gameDescription)
    }
}