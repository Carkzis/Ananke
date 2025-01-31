package com.carkzis.ananke.data.repository

import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.model.NewGame
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGames(): Flow<List<Game>>

    suspend fun addNewGame(newGame: NewGame)

    fun getCurrentGame(): Flow<CurrentGame>

    suspend fun updateCurrentGame(currentGame: CurrentGame)

    suspend fun removeCurrentGame()
}