package com.example.ananke.di

import android.content.Context
import androidx.room.Room
import com.example.ananke.data.AnankeDatabase
import com.example.ananke.data.DefaultGameRepository
import com.example.ananke.data.GameDao
import com.example.ananke.data.GameRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsGameRepository(gameRepository: DefaultGameRepository): GameRepository
}