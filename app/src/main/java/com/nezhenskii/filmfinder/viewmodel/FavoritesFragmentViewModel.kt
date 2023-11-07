package com.nezhenskii.filmfinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.domain.Interactor
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListLiveData: LiveData<List<Film>>
    @Inject
    lateinit  var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.repo.filmsDatabase
    }

}