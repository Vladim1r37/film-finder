package com.nezhenskii.filmfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.domain.Film
import com.nezhenskii.filmfinder.domain.Interactor

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData:  MutableLiveData<List<Film>> = MutableLiveData()
    private  var interactor: Interactor = App.instance.interactor

    init {
        interactor.repo.filmsDatabase = filmsListLiveData
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsListLiveData.postValue(films)
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