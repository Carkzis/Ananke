package com.carkzis.ananke

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.toCurrentGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.game.GameViewModel
import com.carkzis.ananke.ui.screens.game.GamingState
import com.carkzis.ananke.utils.GameStateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
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

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        viewModel = GameViewModel(GameStateUseCase(gameRepository), gameRepository)
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

    fun dummyGames() = listOf(
        Game("abc", "My First Game", "It is the first one."),
        Game("def", "My Second Game", "It is the second one."),
        Game("ghi", "My Third Game", "It is the third one.")
    )
}