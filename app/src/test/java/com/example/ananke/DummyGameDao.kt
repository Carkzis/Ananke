package com.example.ananke

import com.example.ananke.data.GameDao
import com.example.ananke.data.GameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DummyGameDao : GameDao {
    private var games = MutableStateFlow(dummyGameEntities)

    override fun getGames(): Flow<List<GameEntity>> = games

    override suspend fun upsertGames(gameEntities: List<GameEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun insertGame(game: GameEntity) {
        TODO("Not yet implemented")
    }
}

val dummyGameEntities = listOf(
    GameEntity("1", "My First Game", "It is the first one."),
    GameEntity("2", "My Second Game", "It is the second one."),
    GameEntity("3", "My Third Game", "It is the third one.")
)