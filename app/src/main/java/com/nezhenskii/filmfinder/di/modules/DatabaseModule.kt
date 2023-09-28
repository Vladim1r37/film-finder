package com.nezhenskii.filmfinder.di.modules

import com.nezhenskii.filmfinder.data.MainRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideRepository() = MainRepository()
}