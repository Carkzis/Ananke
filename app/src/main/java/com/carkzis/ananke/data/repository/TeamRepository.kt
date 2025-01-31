package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.model.User
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    fun getUsers(): Flow<List<User>>
    fun getTeamMembers(gameId: Long): Flow<List<User>>
    suspend fun addTeamMember(teamMember: User, gameId: Long)
}