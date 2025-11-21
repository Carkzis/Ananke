package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.utils.asGame
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.data.model.NewGame
import com.carkzis.ananke.ui.screens.game.CreatorIdDoesNotMatchException
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import com.carkzis.ananke.ui.screens.game.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.game.InvalidGameException
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class ControllableGameRepository(
    initialCurrentGame: CurrentGame = CurrentGame.EMPTY,
    initialGames: List<Game> = listOf()
) : GameRepository {
    var ADD_GAME_EXISTS = false
    var ENTRY_GAME_EXISTS = true
    var ENTRY_GAME_INVALID = false
    var ENTRY_GENERIC_FAIL = false
    var CREATOR_ID_MISMATCH = false
    var DELETE_GAME_EXISTS = true

    var FAIL_EXIT = false

    private val _games = MutableSharedFlow<List<Game>>(replay = 1)
    private val games get() = _games.replayCache.firstOrNull() ?: listOf()

    private val _currentGame = MutableStateFlow(initialCurrentGame)

    init {
        if (initialGames.isNotEmpty()) {
            _games.tryEmit(initialGames)
        }
    }

    override fun getGames(): Flow<List<Game>> = _games

    override suspend fun addNewGame(newGame: NewGame) {
        if (ADD_GAME_EXISTS) throw GameAlreadyExistsException()

        games.let {
            _games.tryEmit(it + newGame.asGame())
        }
    }

    override fun getCurrentGame(): Flow<CurrentGame> = if (ENTRY_GAME_EXISTS) {
        _currentGame
    } else {
        throw GameDoesNotExistException()
    }

    override suspend fun updateCurrentGame(currentGame: CurrentGame) {
        if (!ENTRY_GAME_EXISTS) throw GameDoesNotExistException()
        if (ENTRY_GAME_INVALID) throw InvalidGameException()
        if (ENTRY_GENERIC_FAIL) throw EnterGameFailedException()

        _currentGame.tryEmit(currentGame)
    }

    override suspend fun removeCurrentGame() {
        if (FAIL_EXIT) throw ExitGameFailedException()

        _currentGame.tryEmit(CurrentGame.EMPTY)
    }

    override suspend fun deleteGame(game: Game) {
        if (CREATOR_ID_MISMATCH) throw CreatorIdDoesNotMatchException()
        if (!DELETE_GAME_EXISTS) throw GameDoesNotExistException()

        games.let {
            val newList = it.filterNot { existingGame -> existingGame.id == game.id }
            _games.tryEmit(newList)
        }
    }

    fun emitGames(newGames: List<Game>) {
        _games.tryEmit(newGames)
    }

    fun emitCurrentGame(currentGame: CurrentGame) {
        _currentGame.tryEmit(currentGame)
    }

}