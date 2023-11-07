package com.nezhenskii.filmfinder.data

import androidx.lifecycle.LiveData
import com.nezhenskii.filmfinder.data.dao.FilmDao
import com.nezhenskii.filmfinder.data.entity.Film
import java.util.concurrent.Executors

class MainRepository(private val filmDao: FilmDao) {
     lateinit var filmsDatabase: LiveData<List<Film>>

    fun putToDb(films: List<Film>) {
        //Запросы в БД должны быть в отдельном потоке
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAll(films)
        }
    }

    fun getAllFromDb(): LiveData<List<Film>> {
        return filmDao.getCachedFilms()
    }

    fun clearAllFromDb() {
        //Удаляем все строки в таблице
        Executors.newSingleThreadExecutor().execute {
        filmDao.clearCache()
        }
    }
}