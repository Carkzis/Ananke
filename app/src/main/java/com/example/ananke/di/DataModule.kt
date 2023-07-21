package com.example.ananke.di

import com.example.ananke.data.DefaultGameRepository
import com.example.ananke.data.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsGameRepository(gameRepository: DefaultGameRepository) : GameRepository
}