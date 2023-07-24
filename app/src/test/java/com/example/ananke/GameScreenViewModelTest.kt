package com.example.ananke

import com.example.ananke.data.DefaultGameRepository
import com.example.ananke.data.dummyGames
import com.example.ananke.ui.screens.GameScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setUp() {
        viewModel = GameScreenViewModel(DefaultGameRepository())
    }

    @Test
    fun `view model displays expected list of games`() = runTest {
        val collection = launch(UnconfinedTestDispatcher()) {
            viewModel.gameList.collect {}
        }
        assertEquals(dummyGames(), viewModel.gameList.value)
        collection.cancel()
    }

}