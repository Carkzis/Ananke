package com.carkzis.ananke.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.carkzis.ananke.ui.screens.EnterGameFailedException
import com.carkzis.ananke.ui.screens.ExitGameFailedException
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val GAME_ID = stringPreferencesKey("game_id")
class DefaultAnankeDataStore @Inject constructor(
    private val preferences: DataStore<Preferences>
) : AnankeDataStore {
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

    override suspend fun removeCurrentGameId() {
        try {
            preferences.edit { preferences ->
                preferences[GAME_ID] = CurrentGame.EMPTY.id
            }
        } catch (exception: IOException) {
            throw ExitGameFailedException()
        }
    }
}