package com.carkzis.ananke.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.carkzis.ananke.data.database.AnankeDatabase
import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.YouDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class YouDaoTest {
    private lateinit var youDao: YouDao
    private lateinit var database: AnankeDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AnankeDatabase::class.java
        ).build()
        youDao = database.youDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `youDao retrieves current character data`() = runTest {
        val characterId = 1L
        val expectedCharacter = CharacterEntity(
            characterId,
            1,
            "Dave",
            "But my name is Jeff."
        )
        youDao.insertCharacter(expectedCharacter)
        val retrievedCharacter = youDao.getCharacterForId(characterId).first()

        assertEquals(expectedCharacter, retrievedCharacter)
    }

}