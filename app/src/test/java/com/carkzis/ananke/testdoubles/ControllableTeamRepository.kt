package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.TeamRepository
import com.carkzis.ananke.data.User
import com.carkzis.ananke.utils.asGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ControllableTeamRepository(
    initialUsers: List<User> = listOf()
) : TeamRepository {

    private val _users = MutableSharedFlow<List<User>>(replay = 1)
    private val users get() = _users.replayCache.firstOrNull() ?: listOf()

    init {
        if (initialUsers.isNotEmpty()) {
            _users.tryEmit(initialUsers)
        }
    }

    override suspend fun getUsers(): Flow<List<User>> = _users

    override suspend fun getTeamMembers(gameId: Long): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun addTeamMember(teamMember: User, gameId: Long) {
        users.let {
            _users.tryEmit(it + teamMember)
        }
    }

}
