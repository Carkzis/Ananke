package com.carkzis.ananke.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.carkzis.ananke.data.database.AnankeDatabase
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.YouDao
import com.carkzis.ananke.testdoubles.dummyCharacterEntities
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.testdoubles.dummyUserEntities
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class YouDaoTest {
    private lateinit var youDao: YouDao
    private lateinit var database: AnankeDatabase

    private val dummyUser = dummyUserEntities.first()
    private val dummyGame = dummyGameEntities.first()
    private val dummyCharacters = dummyCharacterEntities

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AnankeDatabase::class.java
        ).build()
        youDao = database.youDao()

        runBlocking {
            database.teamDao().insertTeamMember(dummyUser)
            database.gameDao().insertGame(dummyGame)

            dummyCharacters.forEach { dummyCharacter ->
                youDao.insertCharacter(dummyCharacter)
            }
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `youDao retrieves current character data`() = runTest {
        val actualCharacters = youDao.getCharactersForUserId(dummyUser.userId).first()

        assertEquals(dummyCharacters, actualCharacters)
    }

    @Test
    fun `youDao updates current character data`() = runTest {
        val updatedCharacter = dummyCharacters.first().copy(characterName = "Jeff", characterBio = "Or was is Dave?")
        val expectedCharacters = dummyCharacters.toMutableList().also {
            it[0] = updatedCharacter
        }
        youDao.updateCharacter(updatedCharacter)

        val actualCharacters = youDao.getCharactersForUserId(dummyUser.userId).first()
        assertEquals(expectedCharacters, actualCharacters)
    }

    @Test
    fun `youDao retrieves user of particular character`() = runTest {
        val actualUserName = youDao.getUserForCharacterId(dummyCharacters.first().characterId)
            .first()
            .userEntity
            .username

        assertEquals(dummyUser.username, actualUserName)
    }

    @Test
    fun `youDao retrieves characters of particular game`() = runTest {
        val actualCharacters = youDao.getCharactersForGameId(dummyGame.gameId)
            .first()
            ?.characterEntities

        assertEquals(dummyCharacters, actualCharacters)
    }

    @Test
    fun `youDao retrieves user for user id`() = runTest {
        youDao.insertUser(dummyUser)

        val actualUser = youDao.getUserForUserId(dummyUser.userId).first()

        assertEquals(dummyUser, actualUser)
    }
}