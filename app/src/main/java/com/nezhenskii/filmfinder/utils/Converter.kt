package com.nezhenskii.filmfinder.utils

import com.nezhenskii.filmfinder.data.entity.TmdbFilm
import com.nezhenskii.filmfinder.domain.Film

object Converter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(
                Film(
                title = it.title,
                poster = it.posterPath,
                description = it.overview,
                rating = it.voteAverage,
                isInFavourites = false
            )
            )
        }
        return result
    }
}