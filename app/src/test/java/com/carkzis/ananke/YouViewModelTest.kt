package com.carkzis.ananke

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.YouViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
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

    @Before
    fun setUp() {
        gameRepository = ControllableGameRepository()
        viewModel = YouViewModel(GameStateUseCase(gameRepository))
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

        Assert.assertEquals(expectedGameTitle, actualGameTitle)

        collection.cancel()
    }

}