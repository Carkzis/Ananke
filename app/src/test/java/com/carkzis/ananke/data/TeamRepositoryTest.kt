package com.carkzis.ananke.data

import com.carkzis.ananke.data.database.TeamDao
import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.network.DefaultNetworkDataSource
import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.data.repository.DefaultTeamRepository
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.testdoubles.ControllableTeamDao
import com.carkzis.ananke.testdoubles.dummyGameEntities
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.ui.screens.team.UserAlreadyExistsException
import com.carkzis.ananke.utils.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var networkDataSource: NetworkDataSource
    private lateinit var teamDao: TeamDao
    private lateinit var teamRepository: TeamRepository
    private lateinit var teamConfiguration: TeamConfiguration

    @Before
    fun setUp() {
        teamDao = ControllableTeamDao()
        networkDataSource = DefaultNetworkDataSource()
        teamConfiguration = TeamConfiguration(teamMemberLimit = Int.MAX_VALUE)
        teamRepository = DefaultTeamRepository(teamDao, networkDataSource, teamConfiguration)
    }

    @Test
    fun `repository provides expected list of users from network`() = runTest {
        val expectedUsers = networkDataSource.getUsers().map { it.toDomainUser() }
        val actualUsers = teamRepository.getUsers().first()
        assertEquals(expectedUsers, actualUsers)
    }

    @Test
    fun `repository adds particular user to a particular game in database`() = runTest {
        val expectedTeamMember = networkDataSource.getUsers().first().toDomainUser()
        val expectedGameId = dummyGameEntities.random().gameId
        teamRepository.addTeamMember(expectedTeamMember, expectedGameId)

        assertTrue(getUserEntitiesAsDomainObjects(expectedGameId).contains(expectedTeamMember))
    }

    @Test
    fun `repository adds additional game to existing user in database`() = runTest {
        val expectedTeamMember = networkDataSource.getUsers().first().toDomainUser()
        val firstGameId = dummyGameEntities.first().gameId
        val secondGameId = dummyGameEntities.last().gameId
        teamRepository.addTeamMember(expectedTeamMember, firstGameId)
        teamRepository.addTeamMember(expectedTeamMember, secondGameId)

        assertTrue(getUserEntitiesAsDomainObjects(firstGameId).contains(expectedTeamMember))
        assertTrue(getUserEntitiesAsDomainObjects(secondGameId).contains(expectedTeamMember))
    }

    @Test
    fun `repository adds additional user to existing game in database`() = runTest {
        val firstTeamMember = networkDataSource.getUsers()[0].toDomainUser()
        val secondTeamMember = networkDataSource.getUsers()[1].toDomainUser()
        val expectedGameId = dummyGameEntities.first().gameId

        teamRepository.addTeamMember(firstTeamMember, expectedGameId)
        teamRepository.addTeamMember(secondTeamMember, expectedGameId)

        assertTrue(getUserEntitiesAsDomainObjects(expectedGameId).contains(firstTeamMember))
        assertTrue(getUserEntitiesAsDomainObjects(expectedGameId).contains(secondTeamMember))
    }

    @Test
    fun `repository gets list of users for a particular game`() = runTest {
        val allUsers = networkDataSource.getUsers().map { it.toDomainUser() }
        val gameOneUsers = allUsers.take(3)
        val gameOneId = 1L
        val gameTwoUsers = allUsers.takeLast(3)
        val gameTwoId = 2L

        gameOneUsers.forEach {
            teamRepository.addTeamMember(it, gameOneId)
        }

        gameTwoUsers.forEach {
            teamRepository.addTeamMember(it, gameTwoId)
        }

        val actualUsersForGameOne = teamRepository.getTeamMembers(gameOneId).first()
        assertEquals(gameOneUsers.toSet(), actualUsersForGameOne.toSet())
    }

    @Test(expected = UserAlreadyExistsException::class)
    fun `repository does not add new user with existing id with exception`() = runTest {
        val expectedTeamMember = networkDataSource.getUsers().first().toDomainUser()
        val newTeamMemberWithSameId = expectedTeamMember.copy(name = "${expectedTeamMember.name}2")

        teamRepository.addTeamMember(expectedTeamMember, 1)
        teamRepository.addTeamMember(newTeamMemberWithSameId, 1)
    }

    @Test(expected = TooManyUsersInTeamException::class)
    fun `repository does not add more users than cap with exception`() = runTest {
        val memberLimit = 5
        val exceededMemberLimit = memberLimit + 1
        teamConfiguration.teamMemberLimit = memberLimit
        val membersAboveCap = networkDataSource.getUsers()
            .take(exceededMemberLimit)
            .map { it.toDomainUser() }

        membersAboveCap.forEach {
            teamRepository.addTeamMember(it, 1)
        }
    }

    @Test
    fun `repository deletes all team members for a game`() = runTest {
        val expectedGameId = dummyGameEntities.first().gameId
        val nonDeletedGameId = dummyGameEntities.last().gameId

        val firstTeamMember = networkDataSource.getUsers()[0].toDomainUser()
        val secondTeamMember = networkDataSource.getUsers()[1].toDomainUser()

        teamRepository.addTeamMember(firstTeamMember, expectedGameId)
        teamRepository.addTeamMember(secondTeamMember, nonDeletedGameId)

        teamRepository.deleteTeamMembersForGame(expectedGameId)

        val teamMembersForDeletedGame = teamRepository.getTeamMembers(expectedGameId).first()
        val teamMembersForNonDeletedGame = teamRepository.getTeamMembers(nonDeletedGameId).first()

        assertTrue(teamMembersForDeletedGame.isEmpty())
        assertEquals(listOf(secondTeamMember), teamMembersForNonDeletedGame)
    }

    @Test
    fun `repository deletes particular team member`() = runTest {
        val expectedGameId = dummyGameEntities.first().gameId

        val firstTeamMember = networkDataSource.getUsers()[0].toDomainUser()
        val secondTeamMember = networkDataSource.getUsers()[1].toDomainUser()

        teamRepository.addTeamMember(firstTeamMember, expectedGameId)
        teamRepository.addTeamMember(secondTeamMember, expectedGameId)

        teamRepository.deleteTeamMember(firstTeamMember, expectedGameId)

        val remainingTeamMembers = teamRepository.getTeamMembers(expectedGameId).first()

        assertEquals(listOf(secondTeamMember), remainingTeamMembers)
    }

    private suspend fun getUserEntitiesAsDomainObjects(id: Long) =
        teamDao.getTeamMembersForGame(id)
            .first()
            .map {
                it.user.toDomain()
            }
}