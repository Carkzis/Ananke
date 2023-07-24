package com.example.ananke.testdoubles

import com.example.ananke.data.Game
import com.example.ananke.data.GameEntity
import com.example.ananke.data.GameRepository
import com.example.ananke.data.dummyGames
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.replay
import kotlinx.coroutines.flow.update

class DummyGameRepository : GameRepository {

    private val _games = MutableSharedFlow<List<Game>>(replay = 1)

    private val currentGames get() = _games.replayCache.firstOrNull() ?: listOf()

    override fun getGames(): Flow<List<Game>> = _games

    override suspend fun addGame(game: Game) {
        currentGames.let {
            _games.tryEmit(it + game)
        }
    }

    fun emitGames(newGames: List<Game>) {
        _games.tryEmit(newGames)
    }
}