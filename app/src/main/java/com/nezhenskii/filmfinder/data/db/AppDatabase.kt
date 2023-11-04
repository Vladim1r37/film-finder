package com.nezhenskii.filmfinder.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nezhenskii.filmfinder.data.dao.FilmDao
import com.nezhenskii.filmfinder.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}