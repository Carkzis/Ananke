package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultGameRepository @Inject constructor(private val gameDao: GameDao) : GameRepository {
    override fun getGames(): Flow<List<Game>> = gameDao.getGames().map {
        it.map(GameEntity::toDomain)
    }

    override suspend fun addGame(game: Game) {
        gameDao.insertGame(game.toEntity())
    }
}