package com.example.ananke

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ananke.data.AnankeDatabase
import com.example.ananke.data.GameDao
import com.example.ananke.data.dummyGames
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
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

    @Test
    fun `gameDao fetches items in id order`() = runTest {
        val dummyGames = dummyGames()
        // gameDao.insertGames()
    }
}