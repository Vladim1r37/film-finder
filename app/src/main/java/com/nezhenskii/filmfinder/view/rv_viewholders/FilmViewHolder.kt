package com.nezhenskii.filmfinder.view.rv_viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nezhenskii.filmfinder.data.ApiConstants
import com.nezhenskii.filmfinder.databinding.FilmItemBinding
import com.nezhenskii.filmfinder.data.entity.Film


class FilmViewHolder(private val binding: FilmItemBinding) : RecyclerView.ViewHolder(binding.root) {

    //В этом методе кладем данные из film в наши view
    fun bind(film: Film) {
        //Устанавливаем заголовок
        binding.title.text = film.title
        //Устанавливаем постер
        //Указываем контейнер, в которм будет "жить" наша картинка
        Glide.with(itemView)
            //Загружаем сам ресурс
            .load(ApiConstants.IMAGES_URL + "w342" + film.poster)
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