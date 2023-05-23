package com.nezhenskii.filmfinder

import androidx.recyclerview.widget.DiffUtil

class FilmDiff(val oldList: List<Film>, val newList: List<Film>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFilm = oldList[oldItemPosition]
        val newFilm = newList[oldItemPosition]
        return oldFilm.poster == newFilm.poster &&
                oldFilm.description == newFilm.description
    }
}