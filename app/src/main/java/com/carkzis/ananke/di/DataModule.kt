package com.carkzis.ananke.di

import com.carkzis.ananke.data.AnankeDataStore
import com.carkzis.ananke.data.DataStoreWrapper
import com.carkzis.ananke.data.DefaultGameRepository
import com.carkzis.ananke.data.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsGameRepository(gameRepository: DefaultGameRepository): GameRepository

    @Binds
    fun bindsDataStoreWrapper(anankeDataStore: AnankeDataStore): DataStoreWrapper
}