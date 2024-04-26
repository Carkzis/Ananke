package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.utils.asGame
import com.carkzis.ananke.data.CurrentGame
import com.carkzis.ananke.data.Game
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.NewGame
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import com.carkzis.ananke.ui.screens.game.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.game.InvalidGameException
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class ControllableGameRepository(initialCurrentGame: CurrentGame = CurrentGame.EMPTY) : GameRepository {

    var gameExistsWhenAddingGame = false
    var gameIsInvalid = false
    var gameExistsWhenEnteringGame = true
    var failToEnterGameOther = false
    var failToExitGame = false

    private val _games = MutableSharedFlow<List<Game>>(replay = 1)
    private val games get() = _games.replayCache.firstOrNull() ?: listOf()

    private val _currentGame = MutableStateFlow(initialCurrentGame)

    override fun getGames(): Flow<List<Game>> = _games

    override suspend fun addNewGame(newGame: NewGame) {
        if (gameExistsWhenAddingGame) throw GameAlreadyExistsException()

        games.let {
            _games.tryEmit(it + newGame.asGame())
        }
    }

    override fun getCurrentGame(): Flow<CurrentGame> = if (gameExistsWhenEnteringGame) {
        _currentGame
    } else {
        throw GameDoesNotExistException()
    }

    override suspend fun updateCurrentGame(currentGame: CurrentGame) {
        if (gameIsInvalid) throw InvalidGameException()
        if (!gameExistsWhenEnteringGame) throw GameDoesNotExistException()
        if (failToEnterGameOther) throw EnterGameFailedException()

        _currentGame.tryEmit(currentGame)
    }

    override suspend fun removeCurrentGame() {
        if (failToExitGame) throw ExitGameFailedException()

        _currentGame.tryEmit(CurrentGame.EMPTY)
    }

    fun emitGames(newGames: List<Game>) {
        _games.tryEmit(newGames)
    }

    fun emitCurrentGame(currentGame: CurrentGame) {
        _currentGame.tryEmit(currentGame)
    }

}