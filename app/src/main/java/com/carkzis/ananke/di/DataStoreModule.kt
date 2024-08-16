package com.carkzis.ananke.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesAnankeDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(IO + SupervisorJob()),
        ) {
            context.preferencesDataStoreFile(USER_PREFERENCES)
        }

}