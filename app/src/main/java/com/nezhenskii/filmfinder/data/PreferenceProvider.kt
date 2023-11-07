package com.nezhenskii.filmfinder.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    //Получаем контекст приложения
    private val appContext = context.applicationContext

    //Создаем экземпляр SharedPreferences
    private val preference: SharedPreferences = appContext.getSharedPreferences(
        "settings",
        Context.MODE_PRIVATE
    )

    init {
        //При первом запуске приложения инициализируем дефолтные настройки
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit { putLong(KEY_LAST_CALL_TO_API, 0) }
            preference.edit { putInt(KEY_CURRENT_PAGE, 1) }
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }

        }
    }

    //Сохраняем категорию
    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
    }

    //Сохраняем время последнего запроса на сервер
    fun saveLastCallTime(lastTime: Long ) {
        preference.edit { putLong(KEY_LAST_CALL_TO_API, lastTime)}
    }

    //Забираем время последнего запроса на сервер
    fun getLastCallTime(): Long {
        return preference.getLong(KEY_LAST_CALL_TO_API, 0)
    }

    //Сохраняем текущую страницу
    fun saveCurrentPage(page: Int) {
        preference.edit { putInt(KEY_CURRENT_PAGE, page) }
    }

    //Забираем текущую страницу
    fun getCurrentPage(): Int {
        return preference.getInt(KEY_CURRENT_PAGE, 1)
    }

    //Забираем категорию
    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    //Подключаем listener
    fun setListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    //Ключи для настроек
    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"
        private const val KEY_LAST_CALL_TO_API = "last_call"
        private const val KEY_CURRENT_PAGE = "current_page"
    }
}