package com.carkzis.ananke.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TeamDaoTest {
    private lateinit var teamDao: TeamDao
    private lateinit var database: AnankeDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AnankeDatabase::class.java
        ).build()
        teamDao = database.teamDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `teamDao fetches all users`() {

    }

}