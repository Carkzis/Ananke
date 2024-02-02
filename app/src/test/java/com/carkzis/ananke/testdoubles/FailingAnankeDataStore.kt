package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.AnankeDataStore
import com.carkzis.ananke.ui.screens.EnterGameFailedException
import com.carkzis.ananke.ui.screens.ExitGameFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FailingAnankeDataStore(private val dataStoreFailure: DataStoreFailure) : AnankeDataStore {
    override suspend fun currentGameId(): String? = null

    override suspend fun setCurrentGameId(gameId: String) {
        if (dataStoreFailure == DataStoreFailure.ENTER_GAME) throw EnterGameFailedException()
    }

    override suspend fun removeCurrentGameId() {
        if (dataStoreFailure == DataStoreFailure.EXIT_GAME) throw ExitGameFailedException()
    }
}

enum class DataStoreFailure {
    ENTER_GAME,
    EXIT_GAME
}