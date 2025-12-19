package com.carkzis.ananke.data.repository

import android.R.attr.value
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.carkzis.ananke.data.TeamConfiguration
import com.carkzis.ananke.data.database.TeamDao
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.data.database.UserEntityWithGames
import com.carkzis.ananke.data.database.UserGameCrossRef
import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.data.database.toDomain
import com.carkzis.ananke.data.model.toEntity
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.ui.screens.team.UserAlreadyExistsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DefaultTeamRepository @Inject constructor(
    private val teamDao: TeamDao,
    private val networkDataSource: NetworkDataSource,
) : TeamRepository {
    private val _teamConfiguration = MutableStateFlow(TeamConfiguration())
    override val teamConfiguration: StateFlow<TeamConfiguration> = _teamConfiguration

    override fun getUsers() = flow {
        emit(networkDataSource.getUsers().map { it.toDomainUser() })
    }

    override fun getTeamMembers(gameId: Long) = teamDao.getTeamMembersForGame(gameId).map {
        it.map(UserEntityWithGames::toDomain)
    }

    override fun updateTeamConfiguration(config: TeamConfiguration) {
        _teamConfiguration.update { config }
    }

    override suspend fun addTeamMember(teamMember: User, gameId: Long) {
        val teamMembersForGame = teamDao.getTeamMembersForGame(gameId).first()
        if (teamMembersForGame.size >= _teamConfiguration.value.teamMemberLimit) {
            throw TooManyUsersInTeamException(_teamConfiguration.value.teamMemberLimit)
        }

        try {
            teamDao.insertTeamMember(teamMember.toEntity())
        } catch (e: SQLiteConstraintException) {
            val existingTeamMember = teamDao.getTeamMembers().first().first {
                it.userId == teamMember.id
            }
            if (existingTeamMember.username != teamMember.name) {
                throw UserAlreadyExistsException()
            }
        } finally {
            teamDao.insertOrIgnoreUserGameCrossRefEntities(listOf(UserGameCrossRef(gameId, teamMember.id)))
        }
    }

    override suspend fun deleteTeamMembersForGame(gameId: Long) {
        teamDao.deleteTeamMembersForGame(gameId)
    }

    override suspend fun deleteTeamMember(teamMember: User, gameId: Long) {
        teamDao.deleteTeamMember(teamMember.id, gameId)
    }
}