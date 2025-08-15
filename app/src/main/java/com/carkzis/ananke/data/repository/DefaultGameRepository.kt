package com.carkzis.ananke.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.carkzis.ananke.data.database.AnankeDataStore
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.data.model.Game
import com.carkzis.ananke.data.database.GameDao
import com.carkzis.ananke.data.database.GameEntity
import com.carkzis.ananke.data.model.NewGame
import com.carkzis.ananke.data.database.toDomainCurrent
import com.carkzis.ananke.data.database.toDomainListing
import com.carkzis.ananke.data.model.toEntity
import com.carkzis.ananke.ui.screens.game.CreatorIdDoesNotMatchException
import com.carkzis.ananke.ui.screens.game.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.game.InvalidGameException
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultGameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val anankeDataStore: AnankeDataStore? = null
) : GameRepository {
    override fun getGames(): Flow<List<Game>> = gameDao.getGames().map {
        it.map(GameEntity::toDomainListing)
    }

    override suspend fun addNewGame(newGame: NewGame) {
        try {
            gameDao.insertGame(newGame.toEntity())
        } catch (e: SQLiteConstraintException) {
            throw GameAlreadyExistsException()
        }
    }

    override fun getCurrentGame(): Flow<CurrentGame> = flow {
        anankeDataStore?.currentGameId()?.collect { currentGameId ->
            if (currentGameId == CurrentGame.EMPTY.id || currentGameId == null) {
                emit(CurrentGame.EMPTY)
            } else {
                val currentGame = gameDao.getGame(currentGameId).first() ?: throw GameDoesNotExistException()
                emit(currentGame.toDomainCurrent())
            }
        } ?: CurrentGame.EMPTY.id
    }

    override suspend fun updateCurrentGame(currentGame: CurrentGame) {
        validateCurrentGame(currentGame)
        anankeDataStore?.setCurrentGameId(currentGame.id)
    }

    override suspend fun removeCurrentGame() {
        anankeDataStore?.removeCurrentGameId()
    }

    override suspend fun deleteGame(game: Game) {
        if (anankeDataStore?.currentUserId()?.first() != game.creatorId) {
            throw CreatorIdDoesNotMatchException()
        }
        gameDao.getGame(game.id).first() ?: throw GameDoesNotExistException()
        gameDao.deleteGame(game.id)
    }

    private fun validateCurrentGame(currentGame: CurrentGame) {
        if (currentGame.id == CurrentGame.EMPTY.id) {
            throw InvalidGameException()
        }
    }
}
