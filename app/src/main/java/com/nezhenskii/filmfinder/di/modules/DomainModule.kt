package com.nezhenskii.filmfinder.di.modules

import com.nezhenskii.filmfinder.data.MainRepository
import com.nezhenskii.filmfinder.data.TmdbApi
import com.nezhenskii.filmfinder.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {
    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi) = Interactor(
        repo = repository, retrofitService = tmdbApi)
}