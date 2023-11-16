package com.nezhenskii.filmfinder.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.nezhenskii.filmfinder.R
import com.nezhenskii.filmfinder.data.ApiConstants
import com.nezhenskii.filmfinder.databinding.FragmentDetailsBinding
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.viewmodel.DetailsFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding!!
    private val viewmodel by lazy {
        ViewModelProvider.NewInstanceFactory().create(DetailsFragmentViewModel::class.java)
    }
    private val scope = CoroutineScope(Dispatchers.IO)

    lateinit var film: Film

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        film = arguments?.get("film") as Film
        binding.detailsToolbar.title = film.title
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + "w780" + film.poster)
            .centerCrop()
            .into(binding.detailsPoster)
        binding.detailsDescription.text = film.description
        binding.detailsFabFavourites.setImageResource(
            if (film.isInFavourites) R.drawable.ic_round_favorite_24
            else R.drawable.ic_round_favorite_border
        )
        binding.detailsFabFavourites.setOnClickListener {
            if (!film.isInFavourites) {
                binding.detailsFabFavourites.setImageResource(R.drawable.ic_round_favorite_24)
                film.isInFavourites = true
            } else {
                binding.detailsFabFavourites.setImageResource(R.drawable.ic_round_favorite_border)
                film.isInFavourites = false
            }

        }
        binding.detailsFabShare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${film.title} \n\n ${film.description}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }
    }

    //Узнаем, было ли получено разрешение ранее
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    //Запрашиваем разрешение
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {
        //Проверяем версию системы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Создаем объект для передачи данных
            val contentValues = ContentValues().apply {
                //Составляем информацию для файла (имя, тип, дата создания, куда сохранять и т.д.)
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.DISPLAY_NAME, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmFinderApp")
            }
            //Получаем ссылку на объект Content resolver, который помогает передавать информацию из приложения вовне
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            //Открываем канал для записи на диск
            val outputStream = contentResolver.openOutputStream(uri!!)
            //Передаем нашу картинку
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
            //Закрываем поток
            outputStream.close()
        } else {
            //То же, но для более старых версий ОС
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val filename = "IMG_${System.currentTimeMillis()}.jpg"
            val image = File(imagesDir, filename)
            val fos: OutputStream?
            fos = FileOutputStream(image)
            fos.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        }
    }

    private fun performAsyncLoadOfPoster() {
        //Проверяем есть ли разрешение
        if (!checkPermission()) {
            //Если нет, то запрашиваем и выходим из метода
            requestPermission()
            return
        }
        //Создаем родительский скоуп с диспатчером Main потока, так как будем взаимодействовать с UI
        MainScope().launch {
            //Включаем Прогресс-бар
            binding.progressBar.isVisible = true
            //Создаем через async, так как нам нужен результат от работы, то есть Bitmap
            val job = scope.async {
                viewmodel.loadWallpaper(ApiConstants.IMAGES_URL + "original" + film.poster)
            }
            //Сохраняем в галерею, как только файл загрузится
            saveToGallery(job.await())
            //Выводим снекбар с кнопкой перейти в галерею
            Snackbar.make(
                binding.root,
                R.string.downloaded_to_gallery,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.open) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.type = "image/*"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .show()
            //Отключаем Прогресс-бар
            binding.progressBar.isVisible = false
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}