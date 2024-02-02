package com.carkzis.ananke

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.toCurrentGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.GameScreenViewModel
import com.carkzis.ananke.ui.screens.GamingState
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
class GameScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: GameScreenViewModel
    private lateinit var gameRepository: ControllableGameRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        viewModel = GameScreenViewModel(gameRepository)
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
    fun `view model provides out of game state when no current game`() = runTest {
        val expectedCurrentGamingState = GamingState.OutOfGame

        val actualCurrentGamingState = viewModel.gamingState.value
        assertEquals(expectedCurrentGamingState, actualCurrentGamingState)
    }

    @Test
    fun `view model updates state to in game with current game`() = runTest {
        val expectedCurrentGame = dummyGames().first().toCurrentGame()
        viewModel.enterGame(expectedCurrentGame)

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        val actualCurrentGame = (viewModel.gamingState.value as GamingState.InGame).currentGame
        assertEquals(expectedCurrentGame, actualCurrentGame)

        collection.cancel()
    }

    fun dummyGames() = listOf(
        Game("abc", "My First Game", "It is the first one."),
        Game("def", "My Second Game", "It is the second one."),
        Game("ghi", "My Third Game", "It is the third one.")
    )
}