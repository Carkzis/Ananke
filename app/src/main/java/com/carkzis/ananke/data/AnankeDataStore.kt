package com.carkzis.ananke.data

import kotlinx.coroutines.flow.Flow

interface AnankeDataStore {
    val data: Flow<String?>
    suspend fun setCurrentGameId(gameId: String)
}