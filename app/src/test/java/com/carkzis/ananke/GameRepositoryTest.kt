package com.carkzis.ananke

import com.carkzis.ananke.data.DefaultGameRepository
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameDao
import com.carkzis.ananke.data.GameEntity
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.toDomain
import com.carkzis.ananke.testdoubles.ControllableGameDao
import com.carkzis.ananke.testdoubles.dummyGameEntities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        gameDao = ControllableGameDao()
        gameRepository = DefaultGameRepository(gameDao)
    }

    @Test
    fun `repository provides expected list of games`() = runTest {
        val expectedGames = dummyGameEntities.map(GameEntity::toDomain)
        val actualGames = getGamesEntitiesAsDomainObjects()
        assertEquals(expectedGames, actualGames)
    }

    @Test
    fun `repository adds game to database`() = runTest {
        val newGame = Game("anId", "aName", "aDescription")
        gameRepository.addGame(newGame)
        assertTrue(getGamesEntitiesAsDomainObjects().contains(newGame))
    }

    private suspend fun getGamesEntitiesAsDomainObjects() =
        gameDao.getGames()
            .first()
            .map(GameEntity::toDomain)
}