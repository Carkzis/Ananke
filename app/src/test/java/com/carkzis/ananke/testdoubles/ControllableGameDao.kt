package com.carkzis.ananke.testdoubles

import android.database.sqlite.SQLiteConstraintException
import com.carkzis.ananke.data.GameDao
import com.carkzis.ananke.data.GameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class ControllableGameDao : GameDao {
    private var games = MutableStateFlow(dummyGameEntities)

    override fun getGames(): Flow<List<GameEntity>> = games

    override fun getGame(gameId: String): Flow<GameEntity> = flow {
        emit(
            games.value.first {
                it.id.toString() == gameId
            }
        )
    }

    override suspend fun upsertGames(gameEntities: List<GameEntity>) {
        games.update { previousValues ->
            (previousValues + gameEntities)
                .distinctBy(GameEntity::id)
                .sortedWith(idDescending())
        }
    }

    override suspend fun insertGame(game: GameEntity) {
        games.update { previousValues ->
            previousValues.forEach {
                if (game.name == it.name || game.id == it.id) {
                    throw SQLiteConstraintException()
                }
            }
            (previousValues + game)
                .sortedWith(idDescending())
        }
    }

    private fun idDescending() = compareBy(GameEntity::id).reversed()
}


val dummyGameEntities = listOf(
    GameEntity(1L, "My First Game", "It is the first one."),
    GameEntity(2L, "My Second Game", "It is the second one."),
    GameEntity(3L, "My Third Game", "It is the third one.")
)