package com.carkzis.ananke.data

import android.database.sqlite.SQLiteConstraintException
import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.network.toDomainUser
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.ui.screens.team.UserAlreadyExistsException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DefaultTeamRepository(
    private val teamDao: TeamDao,
    private val networkDataSource: NetworkDataSource,
    private val configuration: TeamConfiguration = TeamConfiguration()
) : TeamRepository {
    override suspend fun getUsers() = flow {
        emit(networkDataSource.getUsers().map { it.toDomainUser() })
    }

    override suspend fun addTeamMember(teamMember: User, gameId: Long) {
        val teamMembersForGame = teamDao.getTeamMembersForGame(gameId).first()
        if (teamMembersForGame.size >= configuration.teamMemberLimit) {
            throw TooManyUsersInTeamException(configuration.teamMemberLimit)
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
}