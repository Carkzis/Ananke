package com.carkzis.ananke

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.dummyGames
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.GameScreenViewModel
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
    fun `view model can add game to repository`() = runTest {
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }

        val newGame = Game("anId", "aName", "aDescription")
        viewModel.addGame(newGame)
        assertTrue(viewModel.gameList.value.contains(newGame))

        collection.cancel()
    }

}