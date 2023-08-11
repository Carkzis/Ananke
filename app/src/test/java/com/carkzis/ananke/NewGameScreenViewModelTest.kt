package com.carkzis.ananke

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.testdoubles.ControllableGameRepository
import com.carkzis.ananke.ui.screens.NewGameScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
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

}