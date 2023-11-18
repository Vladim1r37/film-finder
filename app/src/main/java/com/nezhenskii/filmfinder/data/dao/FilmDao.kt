package com.nezhenskii.filmfinder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nezhenskii.filmfinder.data.entity.Film
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    //Запрос на всю таблицу
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Flow<List<Film>>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    //Очищаем кэшированные данные
    @Query("DELETE FROM cached_films")
    fun clearCache()

    //Обновляем состояние элемента БД
    @Update
    fun updateFilm(item: Film)
}