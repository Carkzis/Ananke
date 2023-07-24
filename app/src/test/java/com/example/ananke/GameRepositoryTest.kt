package com.example.ananke

import com.example.ananke.data.DefaultGameRepository
import com.example.ananke.data.GameDao
import com.example.ananke.data.GameRepository
import com.example.ananke.data.dummyGames
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var gameDao: GameDao

    @Before
    fun setUp() {
        gameRepository = DefaultGameRepository()
    }

    @Test
    fun `repository provides expected list of games`() = runTest {
        val games = dummyGames()
        games.forEachIndexed { index, game ->
            assertEquals(game, gameRepository.games.first()[index])
        }
    }

}