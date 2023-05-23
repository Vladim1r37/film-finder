package com.nezhenskii.filmfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    val filmsDataBase = listOf(
        Film("Se7en", R.drawable.poster_1, "Two detectives, a rookie and a veteran, hunt a serial killer who uses the seven deadly sins as his motives."),
        Film("Fight Club", R.drawable.poster_2, "An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more."),
        Film("Pulp Fiction", R.drawable.poster_3, "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption."),
        Film("The Shawshank Redemption", R.drawable.poster_4, "Over the course of several years, two convicts form a friendship, seeking consolation and, eventually, redemption through basic compassion."),
        Film("The Lord of the Rings: The Return of the King", R.drawable.poster_5, "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring."),
        Film("The Matrix", R.drawable.poster_6, "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence."),
        Film("Men in Black", R.drawable.poster_7, "A police officer joins a secret organization that polices and monitors extraterrestrial interactions on Earth."),
        Film("The Green Mile", R.drawable.poster_8, "A tale set on death row in a Southern jail, where gentle giant John possesses the mysterious power to heal people's ailments. When the lead guard, Paul, recognizes John's gift, he tries to help stave off the condemned man's execution."),
        Film("The Big Lebowski", R.drawable.poster_9, "Ultimate L.A. slacker Jeff \"The Dude\" Lebowski, mistaken for a millionaire of the same name, seeks restitution for a rug ruined by debt collectors, enlisting his bowling buddies for help while trying to find the millionaire's missing wife.")
    )
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        main_recycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })
            adapter = filmsAdapter

            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        updateFilmDataBase(filmsDataBase)
    }

    private fun updateFilmDataBase(newFilmDataBase: List<Film>) {
        val oldFilmDataBase = filmsAdapter.getItems()
        val filmDiff = FilmDiff(oldFilmDataBase, newFilmDataBase)
        val diffResult = DiffUtil.calculateDiff(filmDiff)
        filmsAdapter.addItems(newFilmDataBase)
        diffResult.dispatchUpdatesTo(filmsAdapter)
    }
}