package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface AnankeDataStore {
    suspend fun currentGameId(): String?

    suspend fun setCurrentGameId(gameId: String)

    suspend fun removeCurrentGameId()
}