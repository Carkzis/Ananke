package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import kotlinx.coroutines.flow.Flow

class DummyGameRepository : GameRepository {
    override fun getGames(): Flow<List<Game>> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewGame(newGame: NewGame) {
        TODO("Not yet implemented")
    }
}