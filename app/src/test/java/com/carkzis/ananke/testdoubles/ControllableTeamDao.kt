package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.TeamDao
import com.carkzis.ananke.data.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableTeamDao : TeamDao {
    private var teamMembers = MutableStateFlow(listOf<UserEntity>())
    override fun getTeamMembers(gameId: Long): Flow<List<UserEntity>> = flow {
        emit(teamMembers.first().filter { it.gameIds.contains(gameId) })
    }

    override suspend fun insertTeamMember(teamMember: UserEntity) {
        teamMembers.update {
            (it + teamMember)
                .sortedWith(idDescending())
        }
    }

    private fun idDescending() = compareBy(UserEntity::id).reversed()
}