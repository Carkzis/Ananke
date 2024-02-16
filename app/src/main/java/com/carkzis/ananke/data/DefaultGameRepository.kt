package com.carkzis.ananke.data

import android.database.sqlite.SQLiteConstraintException
import com.carkzis.ananke.ui.screens.GameDoesNotExistException
import com.carkzis.ananke.ui.screens.InvalidGameException
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
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
        val currentGameId = anankeDataStore?.currentGameId() ?: CurrentGame.EMPTY.id

        if (currentGameId == CurrentGame.EMPTY.id) {
            emit(CurrentGame.EMPTY)
        } else {
            val currentGame = gameDao.getGame(currentGameId).first() ?: throw GameDoesNotExistException()
            emit(currentGame.toDomainCurrent())
        }
    }

    override suspend fun updateCurrentGame(currentGame: CurrentGame) {
        validateCurrentGame(currentGame)
        anankeDataStore?.setCurrentGameId(currentGame.id)
    }

    override suspend fun removeCurrentGame() {
        anankeDataStore?.removeCurrentGameId()
    }

    private fun validateCurrentGame(currentGame: CurrentGame) {
        if (currentGame.id == CurrentGame.EMPTY.id) {
            throw InvalidGameException()
        }
    }
}
