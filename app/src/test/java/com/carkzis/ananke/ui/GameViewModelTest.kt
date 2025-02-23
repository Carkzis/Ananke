package com.carkzis.ananke.ui

import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.toCurrentGame
import com.carkzis.ananke.data.repository.YouRepository
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.testdoubles.ControllableYouRepository
import com.carkzis.ananke.testdoubles.dummyUserEntities
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import com.carkzis.ananke.ui.screens.game.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.game.GameViewModel
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.ui.screens.game.InvalidGameException
import com.carkzis.ananke.utils.GameStateUseCase
import com.carkzis.ananke.utils.MainDispatcherRule
import com.carkzis.ananke.utils.OnboardUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: GameViewModel
    private lateinit var gameRepository: ControllableGameRepository
    private lateinit var youRepository: ControllableYouRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        youRepository = ControllableYouRepository()
        viewModel = GameViewModel(
            gameStateUseCase = GameStateUseCase(gameRepository),
            onboardUserUseCase = OnboardUserUseCase(youRepository),
            gameRepository = gameRepository
        )
    }

    @Test
    fun `view model displays expected list of games`() = runTest {
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        gameRepository.emitGames(dummyGames())
        assertEquals(dummyGames(), viewModel.gameList.value)

        collection.cancel()
    }

    @Test
    fun `view model provides loading state initially`() = runTest {
        val expectedCurrentGamingState = GamingState.Loading

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)
    }

    @Test
    fun `view model provides out of game state when no current game`() = runTest {
        val expectedCurrentGamingState = GamingState.OutOfGame

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)

        collection.cancel()
    }

    @Test
    fun `view model updates state to in game with current game`() = runTest {
        val expectedCurrentGame = dummyGames().first().toCurrentGame()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        viewModel.enterGame(expectedCurrentGame)

        val actualCurrentGame = (viewModel.gamingState.value as GamingState.InGame).currentGame
        assertEquals(expectedCurrentGame, actualCurrentGame)

        collection.cancel()
    }

    @Test
    fun `view model exits game reverting to state out of current game`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val expectedCurrentGamingState = GamingState.OutOfGame
        viewModel.enterGame(currentGame)

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        viewModel.exitGame()

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when failing to enter game`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.ENTRY_GENERIC_FAIL = true
        viewModel.enterGame(currentGame)

        assertEquals(EnterGameFailedException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when game is invalid`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.ENTRY_GAME_INVALID = true
        viewModel.enterGame(currentGame)

        assertEquals(InvalidGameException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when game does not exist`() = runTest {
        val currentGame = dummyGames().first().toCurrentGame()
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.ENTRY_GAME_EXISTS = false
        viewModel.enterGame(currentGame)

        assertEquals(GameDoesNotExistException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model sends toast message about when failing to exit game`() = runTest {
        val messages = mutableListOf<String>()

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.message.collect { messages.add(it) }
        }

        gameRepository.FAIL_EXIT = true
        viewModel.exitGame()

        assertEquals(ExitGameFailedException().message, messages.firstOrNull())
        assertEquals(1, messages.size)

        collection.cancel()
    }

    @Test
    fun `view model creates a new user for the device if unavailable`() = runTest {
        assertEquals(youRepository.currentUser, dummyUserEntities.first().toDomain())
    }

    fun dummyGames() = listOf(
        Game("abc", "My First Game", "It is the first one.", "1"),
        Game("def", "My Second Game", "It is the second one.", "1"),
        Game("ghi", "My Third Game", "It is the third one.", "1")
    )
}