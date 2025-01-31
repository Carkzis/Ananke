package com.carkzis.ananke

import android.content.Context
import androidx.room.Room
import com.carkzis.ananke.data.database.AnankeDatabase
import com.carkzis.ananke.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    @Singleton
    @Provides
    fun providesInMemoryDatabase(@ApplicationContext context: Context): AnankeDatabase {
        return Room.inMemoryDatabaseBuilder(context, AnankeDatabase::class.java).build()
    }
}