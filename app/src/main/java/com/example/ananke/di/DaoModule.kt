package com.example.ananke.di

import com.example.ananke.data.AnankeDatabase
import com.example.ananke.data.GameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun bindsGameDao(database: AnankeDatabase): GameDao = database.gameDao()
}