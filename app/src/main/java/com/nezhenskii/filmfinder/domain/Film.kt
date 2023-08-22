package com.nezhenskii.filmfinder.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String,
    var rating: Float = 0f,
    var isInFavourites: Boolean = false
) : Parcelable
