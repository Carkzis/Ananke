package com.carkzis.ananke.di

import com.carkzis.ananke.data.AnankeDatabase
import com.carkzis.ananke.data.GameDao
import com.carkzis.ananke.data.TeamConfiguration
import com.carkzis.ananke.data.TeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesGameDao(database: AnankeDatabase): GameDao = database.gameDao()

    @Provides
    fun providesTeamDao(database: AnankeDatabase): TeamDao = database.teamDao()
}