package com.carkzis.ananke

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.carkzis.ananke.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton
import kotlin.random.Random

private const val USER_PREFERENCES = "test_user_preferences"

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class]
)
object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesAnankeDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(IO + SupervisorJob()),
        ) {
            context.preferencesDataStoreFile("$USER_PREFERENCES-${Random.nextInt()}")
        }
}