package com.carkzis.ananke.data

import com.carkzis.ananke.data.network.DefaultNetworkDataSource
import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.testdoubles.ControllableTeamDao
import com.carkzis.ananke.testdoubles.dummyGameEntities
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

    @Before
    fun setUp() {
        teamDao = ControllableTeamDao()
        networkDataSource = DefaultNetworkDataSource()
        teamRepository = DefaultTeamRepository(teamDao, networkDataSource)
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

    private suspend fun getUserEntitiesAsDomainObjects(id: Long) =
        teamDao.getTeamMembersForGame(id)
            .first()
            .map {
                it.user.toDomain()
            }
}