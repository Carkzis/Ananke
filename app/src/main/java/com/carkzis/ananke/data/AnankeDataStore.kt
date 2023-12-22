package com.carkzis.ananke.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val GAME_ID = stringPreferencesKey("game_id")
class AnankeDataStore @Inject constructor(
    private val preferences: DataStore<Preferences>
) {
    val data = preferences.data.map { preferences ->
        preferences[GAME_ID]
    }

    suspend fun setCurrentGameId(gameId: String) {
        preferences.edit { preferences ->
            preferences[GAME_ID] = gameId
        }
    }
}