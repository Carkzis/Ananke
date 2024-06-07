package com.carkzis.ananke.data

import com.carkzis.ananke.data.network.DefaultNetworkDataSource
import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.utils.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var networkDataSource: NetworkDataSource
    private lateinit var teamRepository: TeamRepository

    @Before
    fun setUp() {
        networkDataSource = DefaultNetworkDataSource()
        teamRepository = DefaultTeamRepository(networkDataSource)
    }

    @Test
    fun `repository provides expected list of users`() = runTest {
        val expectedUsers = networkDataSource.getUsers().map { it.toDomainUser() }
        val actualUsers = teamRepository.getUsers().first()
        assertEquals(expectedUsers, actualUsers)
    }
}