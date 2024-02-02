package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DummyGameRepository : GameRepository {
    override fun getGames(): Flow<List<Game>> = flow {}
    override suspend fun addNewGame(newGame: NewGame) {}
    override fun getCurrentGame(): Flow<CurrentGame> = flow {}
    override suspend fun updateCurrentGame(currentGame: CurrentGame) {}
    override suspend fun removeCurrentGame() {}
}