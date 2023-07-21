package com.example.ananke

import com.example.ananke.ui.screens.GameScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        viewModel = GameScreenViewModel()
    }

    @Test
    fun `view model displays expected list of games`() = runTest {
        assertEquals(listOf("Game 1"), viewModel.gamesList.value)
    }

}