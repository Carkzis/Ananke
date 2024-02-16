package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.asGame
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class ControllableGameRepository : GameRepository {

    var gameExists = false

    private val _games = MutableSharedFlow<List<Game>>(replay = 1)
    private val games get() = _games.replayCache.firstOrNull() ?: listOf()

    private val _currentGame = MutableStateFlow(CurrentGame.EMPTY)

    override fun getGames(): Flow<List<Game>> = _games

    override suspend fun addNewGame(newGame: NewGame) {
        if (gameExists) throw GameAlreadyExistsException()

        games.let {
            _games.tryEmit(it + newGame.asGame())
        }
    }

    override fun getCurrentGame(): Flow<CurrentGame> = _currentGame

    override suspend fun updateCurrentGame(currentGame: CurrentGame) {
        _currentGame.tryEmit(currentGame)
    }

    override suspend fun removeCurrentGame() {
        _currentGame.tryEmit(CurrentGame.EMPTY)
    }

    fun emitGames(newGames: List<Game>) {
        _games.tryEmit(newGames)
    }

}