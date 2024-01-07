package com.nezhenskii.filmfinder.domain

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.nezhenskii.filmfinder.data.API
import com.nezhenskii.filmfinder.data.MainRepository
import com.nezhenskii.filmfinder.data.PreferenceProvider
import com.nezhenskii.filmfinder.data.TmdbApi
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.data.entity.TmdbResultsDto
import com.nezhenskii.filmfinder.viewmodel.HomeFragmentViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(
    val repo: MainRepository, private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider
) {

    var progressBarState: BehaviorSubject<Boolean> = BehaviorSubject.create()

    //В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, которую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        //Показываем ProgressBar
        progressBarState.onNext(true)
        //Метод getDefaultCategoryFromPreferences() будет при запросе получать список из нужной нам
        //категории
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "en-US", page)
            .enqueue(object : Callback<TmdbResultsDto> {
                override fun onResponse(
                    call: Call<TmdbResultsDto>,
                    response: Response<TmdbResultsDto>
                ) {
                    Observable.fromArray(response.body()?.tmdbFilms ?: listOf())
                        .map { list ->
                            val result = mutableListOf<Film>()
                            list.forEach {
                                result.add(
                                    Film(
                                        title = it.title,
                                        poster = it.posterPath ?: "",
                                        description = it.overview,
                                        rating = it.voteAverage,
                                        isInFavourites = false
                                    )
                                )
                            }
                            result.toList()
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            //Кладем фильмы в БД
                            repo.putToDb(it)
                        }
                    //Выключаем ProgressBar
                    progressBarState.onNext(false)
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                    ////В случае провала выключаем ProgressBar
                    progressBarState.onNext(false)
                    callback.onFailure()
                }
            })
    }

    fun getSearchResultFromApi(query: String): Observable<List<Film>> =
        retrofitService.getFilmFromSearch(API.KEY, query, "en-US", 1)
            .map { responce ->
                val list = responce.tmdbFilms
                val result = mutableListOf<Film>()
                list.forEach {
                    result.add(
                        Film(
                            title = it.title,
                            poster = it.posterPath ?: "",
                            description = it.overview,
                            rating = it.voteAverage,
                            isInFavourites = false
                        )
                    )
                }
                result.toList()
            }

    fun getFilmsFromDb(): Observable<List<Film>> = repo.getAllFromDb()

    fun clearDb() = repo.clearAllFromDb()

    fun update(film: Film) {
        repo.updateFilm(film)
    }

    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    fun getLastCallTime() = preferences.getLastCallTime()

    fun saveLastCallTime(lastTime: Long) {
        preferences.saveLastCallTime(lastTime)
    }

    fun saveCurrentPage(page: Int) {
        preferences.saveCurrentPage(page)
    }

    fun getCurrentPage() = preferences.getCurrentPage()

    fun registerListener(listener: OnSharedPreferenceChangeListener) =
        preferences.setListener(listener)
}