package com.nezhenskii.filmfinder.data

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import com.nezhenskii.filmfinder.data.db.DatabaseHelper
import com.nezhenskii.filmfinder.domain.Film

class MainRepository(databaseHelper: DatabaseHelper) {
    var filmsDatabase: MutableLiveData<List<Film>> = MutableLiveData()
    //Инициализируем объект для взаимодействием с БД
    private val sqlDb = databaseHelper.readableDatabase
    //Создаем курсор для обработки запросов из БД
    private lateinit var cursor: Cursor

    fun putToDb(film: Film) {
        //Создаем объект, который будет хранить пары столбец-данные
        val cv = ContentValues()
        cv.apply {
            put(DatabaseHelper.COLUMN_TITLE, film.title)
            put(DatabaseHelper.COLUMN_POSTER, film.poster)
            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DatabaseHelper.COLUMN_RATING, film.rating)
        }
        //Кладем фильм в БД
        sqlDb.insert(DatabaseHelper.TABLE_NAME, null, cv)
    }

    fun getAllFromDb(): List<Film> {
        //Создаем курсор с запросом получить все из таблицы
        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
        //Сюда сохраняем результат
        val result = mutableListOf<Film>()
        //Проверяем, есть ли хоть одна строка в ответе на запрос
        if (cursor.moveToFirst()) {
            //Итерируемся по таблице, пока есть записи, и создаем объект Film
            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)

                result.add(Film(title, poster, description, rating))
            } while (cursor.moveToNext())
        }
        return result
    }

    fun clearAllFromDb() {
        //Удаляем все строки в таблице
        sqlDb.delete(DatabaseHelper.TABLE_NAME, null, null)
    }
}