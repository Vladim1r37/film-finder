package com.nezhenskii.filmfinder.viewmodel

import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.domain.Interactor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesFragmentViewModel : ViewModel() {
    val filmsListData: Flow<List<Film>>
    @Inject
    lateinit  var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListData = interactor.getFilmsFromDb()
    }

}