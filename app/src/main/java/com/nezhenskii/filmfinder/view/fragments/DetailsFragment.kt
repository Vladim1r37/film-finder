package com.nezhenskii.filmfinder.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nezhenskii.filmfinder.R
import com.nezhenskii.filmfinder.databinding.FragmentDetailsBinding
import com.nezhenskii.filmfinder.domain.Film

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
    get() = _binding!!

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
        binding.detailsPoster.setImageResource(film.poster)
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
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}