package com.carkzis.ananke.testdoubles

import android.database.sqlite.SQLiteConstraintException
import com.carkzis.ananke.data.database.GameDao
import com.carkzis.ananke.data.database.GameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableGameDao : GameDao {
    private var games = MutableStateFlow(dummyGameEntities)

    override fun getGames(): Flow<List<GameEntity>> = games

    override fun getGame(gameId: String): Flow<GameEntity?> = flow {
        emit(
            games.value.firstOrNull {
                it.gameId.toString() == gameId
            }
        )
    }

    override suspend fun upsertGames(gameEntities: List<GameEntity>) {
        games.update { previousValues ->
            (gameEntities + previousValues)
                .distinctBy(GameEntity::gameId)
                .sortedWith(idDescending())
        }
    }

    override suspend fun insertGame(game: GameEntity) {
        games.update { previousValues ->
            previousValues.forEach {
                if (game.name == it.name || game.gameId == it.gameId) {
                    throw SQLiteConstraintException()
                }
            }
            (previousValues + game)
                .sortedWith(idDescending())
        }
    }

    private fun idDescending() = compareBy(GameEntity::gameId).reversed()
}
