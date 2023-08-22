package com.nezhenskii.filmfinder.domain

import com.nezhenskii.filmfinder.data.MainRepository

class Interactor(val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDatabase.getData()
}