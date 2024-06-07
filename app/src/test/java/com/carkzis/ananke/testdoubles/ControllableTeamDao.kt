package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.TeamDao
import com.carkzis.ananke.data.UserEntity
import com.carkzis.ananke.data.UserEntityWithGames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableTeamDao : TeamDao {
    private val listOfGameIds = dummyGameEntities
    private var teamMembers = MutableStateFlow(listOf<UserEntity>())
    override fun getTeamMembers(): Flow<List<UserEntity>> = teamMembers

    override suspend fun insertTeamMember(teamMember: UserEntity) {
        teamMembers.update {
            (it + teamMember)
                .sortedWith(idDescending())
        }
    }

    override fun getTeamMembersWithGames(gameId: Long): Flow<List<UserEntityWithGames>> = flow {
        emit(teamMembers.first().map {
            UserEntityWithGames(it, games = listOfGameIds)
        }.filter {
            val gameIds = it.games.map { game ->
                game.gameId
            }
            gameIds.contains(gameId)
        })
    }

    private fun idDescending() = compareBy(UserEntity::userId).reversed()
}