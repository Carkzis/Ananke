package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGames(): Flow<List<Game>>

    suspend fun addNewGame(newGame: NewGame)

    fun getCurrentGame(): Flow<CurrentGame>

    suspend fun updateCurrentGame(currentGame: CurrentGame)
}