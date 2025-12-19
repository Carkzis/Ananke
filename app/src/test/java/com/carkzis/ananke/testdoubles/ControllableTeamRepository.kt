package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.TeamConfiguration
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.model.User
import com.carkzis.ananke.ui.screens.team.TooManyUsersInTeamException
import com.carkzis.ananke.ui.screens.team.UserAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class ControllableTeamRepository(
    initialUsers: List<User> = listOf(),
) : TeamRepository {

    override val teamConfiguration = MutableStateFlow(
        TeamConfiguration())
    private val _users = MutableSharedFlow<List<User>>(replay = 1)
    private val users get() = _users.replayCache.firstOrNull() ?: listOf()

    private val gameToUserMap = mutableMapOf<Long, List<User>>()

    var limit = 4

    init {
        if (initialUsers.isNotEmpty()) {
            _users.tryEmit(initialUsers)
        }
    }

    override fun getUsers(): Flow<List<User>> = _users

    override fun getTeamMembers(gameId: Long): Flow<List<User>> = _users.map { users ->
        users.filter { gameToUserMap.getOrDefault(gameId, listOf()).contains(it) }
    }

    override fun updateTeamConfiguration(config: TeamConfiguration) {
        teamConfiguration.value = config
    }

    override suspend fun addTeamMember(teamMember: User, gameId: Long) {
        users.let {
            val usersWithSameId = it.filter { user ->
                user.id == teamMember.id && user.name != teamMember.name
            }
            if (usersWithSameId.isNotEmpty()) throw UserAlreadyExistsException()

            val currentUsersForGame = gameToUserMap[gameId] ?: listOf()
            if (currentUsersForGame.size == limit) throw TooManyUsersInTeamException(limit)

            val newUsersForGame = currentUsersForGame + teamMember
            gameToUserMap[gameId] = newUsersForGame

            _users.tryEmit((it + teamMember).distinct())
        }
    }

    var teamMembersDeletedCalled = false
    override suspend fun deleteTeamMembersForGame(gameId: Long) {
        teamMembersDeletedCalled = true
    }

    var deletedTeamMember: User? = null
    override suspend fun deleteTeamMember(
        teamMember: User,
        gameId: Long
    ) {
        deletedTeamMember = teamMember
    }


    fun emitUsers(users: List<User>) {
        _users.tryEmit(users)
    }

}
