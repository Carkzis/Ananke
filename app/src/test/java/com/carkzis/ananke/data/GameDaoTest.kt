package com.carkzis.ananke.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.carkzis.ananke.testdoubles.dummyGameEntities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class GameDaoTest {
    private lateinit var gameDao: GameDao
    private lateinit var database: AnankeDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AnankeDatabase::class.java
        ).build()
        gameDao = database.gameDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `gameDao fetches games in id order`() = runTest {
        gameDao.upsertGames(dummyGameEntities.shuffled())
        val gamesInDatabase = gameDao.getGames().first()
        assertEquals(dummyGameEntities.asReversed(), gamesInDatabase)
    }

    @Test
    fun `gameDao fetches game for id`() = runTest {
        gameDao.upsertGames(dummyGameEntities.shuffled())
        val expectedGame = dummyGameEntities.first()
        val id = expectedGame.gameId.toString()

        val actualGame = gameDao.getGame(id).first()

        assertEquals(expectedGame, actualGame)
    }

    @Test
    fun `gameDao fetches null for non-existent game`() = runTest {
        gameDao.upsertGames(dummyGameEntities.shuffled())
        val nonExistentGameId = "47"

        val actualGame = gameDao.getGame(nonExistentGameId).first()
        assertNull(actualGame)
    }

    @Test
    fun `gameDao inserts new game entity`() = runTest {
        val newGame = GameEntity(1L, "aName", "aDescription")
        gameDao.insertGame(newGame)
        assertTrue(gameDao.getGames().first().contains(newGame))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `gameDao cannot insert new game entity with existing name`() = runTest {
        val newGame = GameEntity(1L, "aName", "aDescription")
        val newGameSameName = GameEntity(2L, "aName", "aDescription")
        gameDao.insertGame(newGame)
        gameDao.insertGame(newGameSameName)
    }

}