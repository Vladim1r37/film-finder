package com.nezhenskii.filmfinder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {
    lateinit var film: Film

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        film = arguments?.get("film") as Film
        details_toolbar.title = film.title
        details_poster.setImageResource(film.poster)
        details_description.text = film.description
        details_fab_favourites.setImageResource(
            if (film.isInFavourites) R.drawable.ic_round_favorite_24
        else R.drawable.ic_round_favorite_border
        )
        details_fab_favourites.setOnClickListener {
            if (!film.isInFavourites) {
                details_fab_favourites.setImageResource(R.drawable.ic_round_favorite_24)
                film.isInFavourites = true
            } else {
                details_fab_favourites.setImageResource(R.drawable.ic_round_favorite_border)
                film.isInFavourites = false
            }

        }
        details_fab_share.setOnClickListener {
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
}