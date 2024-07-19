package com.carkzis.ananke.di

import com.carkzis.ananke.data.DefaultAnankeDataStore
import com.carkzis.ananke.data.AnankeDataStore
import com.carkzis.ananke.data.DefaultGameRepository
import com.carkzis.ananke.data.DefaultTeamRepository
import com.carkzis.ananke.data.GameRepository
import com.carkzis.ananke.data.TeamConfiguration
import com.carkzis.ananke.data.TeamRepository
import com.carkzis.ananke.data.network.DefaultNetworkDataSource
import com.carkzis.ananke.data.network.NetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsGameRepository(gameRepository: DefaultGameRepository): GameRepository

    @Binds
    fun bindsTeamRepository(teamRepository: DefaultTeamRepository): TeamRepository

    @Binds
    fun bindsAnankeDataStore(anankeDataStore: DefaultAnankeDataStore): AnankeDataStore

    @Binds
    fun bindsNetworkDataSource(networkDataSource: DefaultNetworkDataSource): NetworkDataSource
}