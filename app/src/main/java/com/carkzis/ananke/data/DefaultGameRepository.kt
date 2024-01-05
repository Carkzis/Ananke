package com.carkzis.ananke.data

import android.database.sqlite.SQLiteConstraintException
import androidx.datastore.preferences.core.emptyPreferences
import com.carkzis.ananke.ui.screens.nugame.GameAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultGameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val anankeDataStore: AnankeDataStore? = null
) : GameRepository {
    override fun getGames(): Flow<List<Game>> = gameDao.getGames().map {
        it.map(GameEntity::toDomain)
    }

    override suspend fun addNewGame(newGame: NewGame) {
        try {
            gameDao.insertGame(newGame.toEntity())
        } catch (e: SQLiteConstraintException) {
            throw GameAlreadyExistsException()
        }
    }

    override fun getCurrentGame(): Flow<CurrentGame> = flow {
        val currentGame = anankeDataStore?.data?.first()
        currentGame?.let { currentGameId ->
            emit(CurrentGame(currentGameId))
        } ?: emit(CurrentGame.ABSENT)
    }

    override suspend fun updateCurrentGame(currentGame: CurrentGame) {
        anankeDataStore?.setCurrentGameId(currentGame.id)
    }

    override suspend fun removeCurrentGame(currentGame: CurrentGame) {
        anankeDataStore?.setCurrentGameId(CurrentGame.ABSENT.id)
    }
}
