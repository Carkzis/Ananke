package com.carkzis.ananke.di

import android.content.Context
import androidx.room.Room
import com.carkzis.ananke.data.AnankeDatabase
import com.carkzis.ananke.data.DefaultGameRepository
import com.carkzis.ananke.data.GameDao
import com.carkzis.ananke.data.GameRepository
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