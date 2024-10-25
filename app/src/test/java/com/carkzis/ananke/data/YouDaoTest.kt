package com.carkzis.ananke.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.carkzis.ananke.data.database.AnankeDatabase
import com.carkzis.ananke.data.database.CharacterEntity
import com.carkzis.ananke.data.database.UserCharacterCrossRef
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.YouDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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

    val dummyUserId = 1L
    private val dummyUser = UserEntity(
        dummyUserId,
        "Mr User"
    )
    private val dummyCharacter = CharacterEntity(
        1,
        dummyUserId,
        "Dave",
        "But my name is Jeff."
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AnankeDatabase::class.java
        ).build()
        youDao = database.youDao()

        runBlocking {
            youDao.insertOrUpdateCharacter(dummyCharacter)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `youDao retrieves current character data`() = runTest {
        val actualCharacter = youDao.getCharacterForId(dummyCharacter.characterId).first()

        assertEquals(dummyCharacter, actualCharacter)
    }

    @Test
    fun `youDao updates current character data`() = runTest {
        val updatedCharacter = dummyCharacter.copy(characterName = "Jeff", characterBio = "Or was is Dave?")
        youDao.insertOrUpdateCharacter(updatedCharacter)

        val actualCharacter = youDao.getCharacterForId(dummyCharacter.characterId).first()
        assertEquals(updatedCharacter, actualCharacter)
    }

    @Test
    fun `youDao adds cross reference for character and user and retrieves user of particular character`() = runTest {
        database.teamDao().insertTeamMember(dummyUser)
        youDao.insertOrIgnoreCharacterUserCrossRefEntities(UserCharacterCrossRef(
            dummyCharacter.characterId,
            dummyUser.userId
        ))

        val actualUserName = youDao.getUserForCharacterId(dummyCharacter.characterId)
            .first()
            .userEntity
            .username

        assertEquals(dummyUser.username, actualUserName)
    }
}