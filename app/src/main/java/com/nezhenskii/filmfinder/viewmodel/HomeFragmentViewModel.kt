package com.nezhenskii.filmfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.domain.Film
import com.nezhenskii.filmfinder.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData: MutableLiveData<List<Film>> = MutableLiveData()

    @Inject
    lateinit var interactor: Interactor
    var page = 1

    init {
        App.instance.dagger.inject(this)
        getFilms()
        interactor.repo.filmsDatabase = filmsListLiveData
    }

    fun getFilms() {
        interactor.getFilmsFromApi(page, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsListLiveData.postValue(films)
                page++
            }

            override fun onFailure() {
            }

        })
    }

    fun getNextPage() {
        interactor.getFilmsFromApi(page, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                val list = filmsListLiveData.value?.toMutableList()
                list?.addAll(films)
                filmsListLiveData.postValue(list)
                page++
            }

            override fun onFailure() {
            }

        })
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}