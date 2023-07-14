package com.nezhenskii.filmfinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nezhenskii.filmfinder.databinding.FilmItemBinding

class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: FilmItemBinding
    private val items = mutableListOf<Film>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
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
        items.clear()
        items.addAll(list)
    }

    fun getItems(): List<Film> {
        return items
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }
}