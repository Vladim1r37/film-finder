package com.nezhenskii.filmfinder.di.modules

import android.content.Context
import com.nezhenskii.filmfinder.data.MainRepository
import com.nezhenskii.filmfinder.data.db.DatabaseHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabaseHelper(context: Context) = DatabaseHelper(context)

    @Provides
    @Singleton
    fun provideRepository(databaseHelper: DatabaseHelper) = MainRepository(databaseHelper)
}