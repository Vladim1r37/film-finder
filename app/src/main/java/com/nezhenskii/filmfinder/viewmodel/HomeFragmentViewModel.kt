package com.nezhenskii.filmfinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData: LiveData<List<Film>>
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    @Inject
    lateinit var interactor: Interactor
    var page = 1

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDb()
        getFilms()
        interactor.repo.filmsDatabase = filmsListLiveData
    }

    fun getFilms() {
        showProgressBar.postValue(true)
        interactor.getFilmsFromApi(page, object : ApiCallback {
            override fun onSuccess() {
                showProgressBar.postValue(false)
                page++
            }

            override fun onFailure() {
                showProgressBar.postValue(false)
            }

        })
    }

    fun getNextPage() {
        interactor.getFilmsFromApi(page, object : ApiCallback {
            override fun onSuccess() {
                page++
            }

            override fun onFailure() {
            }

        })
    }

    fun clearDb() = interactor.clearDb()

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
}