package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGames(): Flow<List<Game>>

    suspend fun addNewGame(newGame: NewGame)
}