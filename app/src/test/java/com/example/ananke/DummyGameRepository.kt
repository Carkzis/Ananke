package com.example.ananke

import com.example.ananke.data.Game
import com.example.ananke.data.GameRepository
import com.example.ananke.data.dummyGames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DummyGameRepository : GameRepository {
    private val games = MutableStateFlow(dummyGames())

    override fun getGames(): Flow<List<Game>> = games
}