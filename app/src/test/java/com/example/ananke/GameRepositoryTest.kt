package com.example.ananke

import com.example.ananke.data.DefaultGameRepository
import com.example.ananke.data.GameRepository
import com.example.ananke.data.dummyGameData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var gameRepository: GameRepository

    @Before
    fun setUp() {
        gameRepository = DefaultGameRepository()
    }

    @Test
    fun `repository provides expected list of games`() = runTest {
        val games = dummyGameData()
        games.forEachIndexed { index, game ->
            assertEquals(game, gameRepository.gamesData.first()[index])
        }
    }
}