package com.nezhenskii.filmfinder.di

import com.nezhenskii.filmfinder.di.modules.DatabaseModule
import com.nezhenskii.filmfinder.di.modules.DomainModule
import com.nezhenskii.filmfinder.di.modules.RemoteModule
import com.nezhenskii.filmfinder.viewmodel.FavoritesFragmentViewModel
import com.nezhenskii.filmfinder.viewmodel.HomeFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
interface AppComponent {
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(favoritesFragmentViewModel: FavoritesFragmentViewModel)
}