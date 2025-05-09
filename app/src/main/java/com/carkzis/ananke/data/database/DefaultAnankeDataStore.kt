package com.carkzis.ananke.data.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.carkzis.ananke.data.model.CurrentGame
import com.carkzis.ananke.ui.screens.game.EnterGameFailedException
import com.carkzis.ananke.ui.screens.game.ExitGameFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val GAME_ID = stringPreferencesKey("game_id")
val USER_ID = stringPreferencesKey("user_id")

class DefaultAnankeDataStore @Inject constructor(
    private val preferences: DataStore<Preferences>
) : AnankeDataStore {
    private val data = preferences.data

    override suspend fun currentGameId() = data.map { preferences ->
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

    override suspend fun currentUserId(): Flow<String?> = data.map { preferences ->
        preferences[USER_ID]
    }

    override suspend fun setCurrentUserId(userId: String) {
        try {
            preferences.edit { preferences ->
                preferences[USER_ID] = userId
            }
        } catch (exception: IOException) {
            throw exception
        }
    }
}