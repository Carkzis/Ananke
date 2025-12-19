package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.TeamConfiguration
import com.carkzis.ananke.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TeamRepository {
    val teamConfiguration: StateFlow<TeamConfiguration>

    fun getUsers(): Flow<List<User>>
    fun getTeamMembers(gameId: Long): Flow<List<User>>

    fun updateTeamConfiguration(config: TeamConfiguration)
    suspend fun addTeamMember(teamMember: User, gameId: Long)
    suspend fun deleteTeamMembersForGame(gameId: Long)

    suspend fun deleteTeamMember(teamMember: User, gameId: Long)
}