package com.carkzis.ananke.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.carkzis.ananke.data.database.AnankeDatabase
import com.carkzis.ananke.data.database.TeamDao
import com.carkzis.ananke.data.database.UserGameCrossRef
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.testdoubles.dummyUserEntities
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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

        runBlocking {
            insertDummyUserEntities()
            insertDummyGameEntities()
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `teamDao fetches all users`() = runTest {
        val usersFromDatabase = teamDao.getTeamMembers().first()
        assertEquals(dummyUserEntities, usersFromDatabase)
    }

    @Test
    fun `teamDao adds cross reference for users and games and retrieves all users for a game`() = runTest {
        val expectedTeamMember = teamDao.getTeamMembers().first().first()
        val gameForCrossRef = dummyGameEntities.first()
        val crossRef = UserGameCrossRef(gameForCrossRef.gameId, expectedTeamMember.userId)
        teamDao.insertOrIgnoreUserGameCrossRefEntities(listOf(crossRef))

        val usersForGame = teamDao.getTeamMembersForGame(gameForCrossRef.gameId).first()
        assertEquals(1, usersForGame.size)
        assertEquals(expectedTeamMember, usersForGame.first().user)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `teamDao cannot insert new user entity with existing userId`() = runTest {
        teamDao.insertTeamMember(dummyUserEntities.first())
    }

    @Test
    fun `teamDao deletes all cross references for a game`() = runTest {
        val teamMember1 = teamDao.getTeamMembers().first().first()
        val teamMember2 = teamDao.getTeamMembers().first().last()

        val gameForCrossRef = dummyGameEntities.first()
        val crossRef1 = UserGameCrossRef(gameForCrossRef.gameId, teamMember1.userId)
        val crossRef2 = UserGameCrossRef(gameForCrossRef.gameId, teamMember2.userId)
        teamDao.insertOrIgnoreUserGameCrossRefEntities(listOf(crossRef1))
        teamDao.insertOrIgnoreUserGameCrossRefEntities(listOf(crossRef2))

        val usersForGameAtStart = teamDao.getTeamMembersForGame(gameForCrossRef.gameId).first()
        assertEquals(2, usersForGameAtStart.size)

        teamDao.deleteTeamMembersForGame(gameForCrossRef.gameId)

        val expactedUsersForGame = teamDao.getTeamMembersForGame(gameForCrossRef.gameId).first()
        assertEquals(0, expactedUsersForGame.size)
    }

    @Test
    fun `teamDao deletes a team member`() = runTest {
        val teamMemberToDelete = teamDao.getTeamMembers().first().first()
        teamDao.deleteTeamMember(teamMemberToDelete)

        val usersAfterDeletion = teamDao.getTeamMembers().first()
        assertEquals(dummyUserEntities.size - 1, usersAfterDeletion.size)
        assertTrue(!usersAfterDeletion.contains(teamMemberToDelete))
    }

    private suspend fun insertDummyUserEntities() {
        dummyUserEntities.forEach {
            teamDao.insertTeamMember(it)
        }
    }

    private suspend fun insertDummyGameEntities() {
        val gameDao = database.gameDao()
        dummyGameEntities.forEach {
            gameDao.insertGame(it)
        }
    }

}