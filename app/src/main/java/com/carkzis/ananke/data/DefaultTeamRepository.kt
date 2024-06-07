package com.carkzis.ananke.data

import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.network.toDomainUser
import kotlinx.coroutines.flow.flow

class DefaultTeamRepository(
    private val teamDao: TeamDao,
    private val networkDataSource: NetworkDataSource
) : TeamRepository {
    override suspend fun getUsers() = flow {
        emit(networkDataSource.getUsers().map { it.toDomainUser() })
    }

    override suspend fun addTeamMember(teamMember: User, gameId: Long) {
        teamDao.insertTeamMember(teamMember.toEntity(listOf(gameId)))
    }
}