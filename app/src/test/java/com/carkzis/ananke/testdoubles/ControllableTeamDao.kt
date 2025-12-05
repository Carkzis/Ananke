package com.carkzis.ananke.testdoubles

import android.database.sqlite.SQLiteConstraintException
import com.carkzis.ananke.data.database.TeamDao
import com.carkzis.ananke.data.database.UserEntity
import com.carkzis.ananke.data.database.UserEntityWithGames
import com.carkzis.ananke.data.database.UserGameCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableTeamDao : TeamDao {
    private val listOfGameIds = dummyGameEntities
    private var teamMembers = MutableStateFlow(listOf<UserEntity>())
    private val crossReferences = mutableListOf<Pair<Long, Long>>()

    override fun getTeamMembers(): Flow<List<UserEntity>> = teamMembers

    override suspend fun insertTeamMember(teamMember: UserEntity) {
        val usersWithSameId = teamMembers.value.filter { it.userId == teamMember.userId }
        if (usersWithSameId.isNotEmpty()) throw SQLiteConstraintException()
        teamMembers.update {
            (it + teamMember)
                .sortedWith(idDescending())
        }
    }

    override suspend fun insertOrIgnoreUserGameCrossRefEntities(userGameCrossReferences: List<UserGameCrossRef>) {
        val crossReferencesAsPairs = userGameCrossReferences.map {
            Pair(
                it.gameId,
                it.userId
            )
        }
        crossReferences.addAll(crossReferencesAsPairs)
    }

    override fun getTeamMembersForGame(gameId: Long): Flow<List<UserEntityWithGames>> = flow {
        emit(teamMembers.first().map {
            UserEntityWithGames(it, games = listOfGameIds)
        }.filter {
            crossReferences.contains(Pair(gameId, it.user.userId))
        })
    }

    override suspend fun deleteTeamMembersForGame(gameId: Long) {
        crossReferences.removeIf {
            it.first == gameId
        }
    }

    override suspend fun deleteTeamMember(userId: Long, gameId: Long) {
        crossReferences.removeIf {
            it.first == gameId && it.second == userId
        }

        teamMembers.update {
            it.filter { currentMember ->
                currentMember.userId != userId
            }.sortedWith(idDescending())
        }

    }

    private fun idDescending() = compareBy(UserEntity::userId).reversed()
}