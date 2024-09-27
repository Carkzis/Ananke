package com.carkzis.ananke.di

import com.carkzis.ananke.data.database.DefaultAnankeDataStore
import com.carkzis.ananke.data.database.AnankeDataStore
import com.carkzis.ananke.data.repository.DefaultGameRepository
import com.carkzis.ananke.data.repository.DefaultTeamRepository
import com.carkzis.ananke.data.repository.GameRepository
import com.carkzis.ananke.data.repository.TeamRepository
import com.carkzis.ananke.data.network.DefaultNetworkDataSource
import com.carkzis.ananke.data.network.NetworkDataSource
import com.carkzis.ananke.data.repository.DefaultYouRepository
import com.carkzis.ananke.data.repository.YouRepository
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
    fun bindsTeamRepository(teamRepository: DefaultTeamRepository): TeamRepository

    @Binds
    fun bindsYouRepository(youRepository: DefaultYouRepository): YouRepository

    @Binds
    fun bindsAnankeDataStore(anankeDataStore: DefaultAnankeDataStore): AnankeDataStore

    @Binds
    fun bindsNetworkDataSource(networkDataSource: DefaultNetworkDataSource): NetworkDataSource
}