package com.carkzis.ananke.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.carkzis.ananke.ui.screens.nugame.EnterGameFailedException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val GAME_ID = stringPreferencesKey("game_id")
class AnankeDataStore @Inject constructor(
    private val preferences: DataStore<Preferences>
) : DataStoreWrapper {
    override val data = preferences.data
        .map { preferences ->
            preferences[GAME_ID]
        }

    override suspend fun setCurrentGameId(gameId: String) {
        try {
            preferences.edit { preferences ->
                preferences[GAME_ID] = gameId
            }
        } catch (exception: IOException) {
            throw EnterGameFailedException()
        }
    }
}