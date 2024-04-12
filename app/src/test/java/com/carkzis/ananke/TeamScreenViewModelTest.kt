package com.carkzis.ananke

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.GameScreenViewModel
import com.carkzis.ananke.ui.screens.TeamScreenViewModel
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
class TeamScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TeamScreenViewModel
    private lateinit var gameRepository: ControllableGameRepository

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        viewModel = TeamScreenViewModel(GameStateUseCase(gameRepository))
    }

    @Test
    fun `view model displays no game title if unavailable`() = runTest {
        val expectedGameTitle = ""
        val actualGameTitle = viewModel.currentGame

        assertEquals(expectedGameTitle, actualGameTitle.name)
    }

    @Test
    fun `view model displays game title if available`() = runTest {
        val expectedGameTitle = "A Game"
        val currentGame = CurrentGame("1", expectedGameTitle, "A Description")

        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gamingState.collect()
        }

        gameRepository.emitCurrentGame(currentGame)
        
        val actualGameTitle = viewModel.currentGame
        assertEquals(expectedGameTitle, actualGameTitle.name)

        collection.cancel()
    }

}