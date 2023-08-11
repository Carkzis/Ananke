package com.carkzis.ananke

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.carkzis.ananke.data.AnankeDatabase
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameDao
import com.carkzis.ananke.data.GameEntity
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.testdoubles.dummyGameEntities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `gameDao inserts new game entity`() = runTest {
        val newGame = GameEntity(1L, "aName", "aDescription")
        gameDao.insertGame(newGame)
        assertTrue(gameDao.getGames().first().contains(newGame))
    }

}