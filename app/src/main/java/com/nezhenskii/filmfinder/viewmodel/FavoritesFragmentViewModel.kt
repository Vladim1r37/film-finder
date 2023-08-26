package com.nezhenskii.filmfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.domain.Film
import com.nezhenskii.filmfinder.domain.Interactor

class FavoritesFragmentViewModel : ViewModel() {
    private  var interactor: Interactor = App.instance.interactor
    val filmsListLiveData:  MutableLiveData<List<Film>> = interactor.repo.filmsDatabase

}