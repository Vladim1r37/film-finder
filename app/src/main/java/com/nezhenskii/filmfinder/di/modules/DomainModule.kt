package com.nezhenskii.filmfinder.di.modules

import android.content.Context
import com.nezhenskii.filmfinder.data.MainRepository
import com.nezhenskii.filmfinder.data.PreferenceProvider
import com.nezhenskii.filmfinder.data.TmdbApi
import com.nezhenskii.filmfinder.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
//Передаем контекст для SharedPreferences через конструктор
class DomainModule(val context: Context) {
    //Нам нужно контекст как-то провайдить, поэтому создаем такой метод
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    //Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideInteractor(
        repository: MainRepository,
        tmdbApi: TmdbApi,
        preferenceProvider: PreferenceProvider
    ) = Interactor(
        repo = repository, retrofitService = tmdbApi, preferences = preferenceProvider
    )
}