package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.asGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ControllableGameRepository : GameRepository {

    var gameExists = false

    private val _games = MutableSharedFlow<List<Game>>(replay = 1)

    private val currentGames get() = _games.replayCache.firstOrNull() ?: listOf()

    override fun getGames(): Flow<List<Game>> = _games

    override suspend fun addNewGame(newGame: NewGame) {
        if (gameExists) throw GameAlreadyExistsException()

        currentGames.let {
            _games.tryEmit(it + newGame.asGame())
        }
    }

    fun emitGames(newGames: List<Game>) {
        _games.tryEmit(newGames)
    }

}