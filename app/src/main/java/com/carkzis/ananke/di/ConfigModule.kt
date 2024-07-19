package com.carkzis.ananke.di

import com.carkzis.ananke.data.TeamConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    @Provides
    fun providesTeamConfiguration(): TeamConfiguration = TeamConfiguration()
}