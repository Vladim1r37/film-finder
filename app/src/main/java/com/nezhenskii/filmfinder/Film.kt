package com.nezhenskii.filmfinder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String,
    var isInFavourites: Boolean = false
) : Parcelable
