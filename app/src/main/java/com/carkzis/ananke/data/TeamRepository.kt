package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getTeamMembers(gameId: Long): Flow<List<User>>
    suspend fun addTeamMember(teamMember: User, gameId: Long)
}