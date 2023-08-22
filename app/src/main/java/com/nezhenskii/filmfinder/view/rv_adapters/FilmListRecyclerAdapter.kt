package com.nezhenskii.filmfinder.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nezhenskii.filmfinder.view.rv_viewholders.FilmViewHolder
import com.nezhenskii.filmfinder.databinding.FilmItemBinding
import com.nezhenskii.filmfinder.domain.Film
import com.nezhenskii.filmfinder.utils.FilmDiff

class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<Film>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilmViewHolder(FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FilmViewHolder -> {
                holder.bind(items[position])
                holder.itemView.rootView.setOnClickListener{
                    clickListener.click(items[position])
                }
            }
        }
    }

    override fun getItemCount() = items.size

    fun addItems(list: List<Film>) {
        val oldFilmDataBase = getItems()
        val filmDiff = FilmDiff(oldFilmDataBase, list)
        val diffResult = DiffUtil.calculateDiff(filmDiff)
        items.clear()
        items.addAll(list)
        diffResult.dispatchUpdatesTo(this)

    }

    fun getItems(): List<Film> {
        return items
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }
}