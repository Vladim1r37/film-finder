package com.nezhenskii.filmfinder.data

import com.nezhenskii.filmfinder.data.dao.FilmDao
import com.nezhenskii.filmfinder.data.entity.Film
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

class MainRepository(private val filmDao: FilmDao) {
    fun putToDb(films: List<Film>) {
            filmDao.insertAll(films)
    }

    fun getAllFromDb(): Flow<List<Film>> {
        return filmDao.getCachedFilms()
    }

    fun clearAllFromDb() {
        //Удаляем все строки в таблице
        Executors.newSingleThreadExecutor().execute {
        filmDao.clearCache()
        }
    }

    fun updateFilm(film: Film) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.updateFilm(film)
        }
    }
}