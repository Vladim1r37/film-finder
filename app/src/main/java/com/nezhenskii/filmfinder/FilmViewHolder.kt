package com.nezhenskii.filmfinder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nezhenskii.filmfinder.databinding.FilmItemBinding


class FilmViewHolder(private var binding: FilmItemBinding) : RecyclerView.ViewHolder(binding.root) {

    //В этом методе кладем данные из film в наши view
    fun bind(film: Film) {
        //Устанавливаем заголовок
        binding.title.text = film.title
        //Устанавливаем постер
        //Указываем контейнер, в которм будет "жить" наша картинка
        Glide.with(itemView)
            //Загружаем сам ресурс
            .load(film.poster)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(binding.poster)
        //Устанавливаем описание
        binding.description.text = film.description
        //Устанавливаем рэйтинг
        binding.ratingDonut.setProgress((film.rating * 10).toInt())
    }
}