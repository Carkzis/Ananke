package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.data.AnankeDataStore
import com.carkzis.ananke.ui.screens.nugame.EnterGameFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FailingAnankeDataStore : AnankeDataStore {
    override val data: Flow<String?> = flow {}
    override suspend fun setCurrentGameId(gameId: String) {
        throw EnterGameFailedException()
    }
}