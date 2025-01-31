package com.carkzis.ananke.di

import com.carkzis.ananke.data.database.AnankeDatabase
import com.carkzis.ananke.data.database.GameDao
import com.carkzis.ananke.data.database.TeamDao
import com.carkzis.ananke.data.database.YouDao
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

    @Provides
    fun providesYouDao(database: AnankeDatabase): YouDao = database.youDao()
}