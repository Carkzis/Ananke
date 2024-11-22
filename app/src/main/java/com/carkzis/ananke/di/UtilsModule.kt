package com.carkzis.ananke.di

import com.carkzis.ananke.utils.RandomCharacterNameGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {
    @Provides
    fun providesCharacterNameGenerator() = RandomCharacterNameGenerator
}