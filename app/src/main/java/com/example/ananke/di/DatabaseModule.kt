package com.carkzis.ananke.di

import android.content.Context
import androidx.room.Room
import com.carkzis.ananke.data.AnankeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesAnankeDatabase(@ApplicationContext context: Context): AnankeDatabase =
        Room.databaseBuilder(context, AnankeDatabase::class.java, "ananke-database").build()
}