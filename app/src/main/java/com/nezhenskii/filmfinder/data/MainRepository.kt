package com.nezhenskii.filmfinder.data

import androidx.lifecycle.MutableLiveData
import com.nezhenskii.filmfinder.domain.Film

class MainRepository {
    var filmsDatabase: MutableLiveData<List<Film>> = MutableLiveData()
}