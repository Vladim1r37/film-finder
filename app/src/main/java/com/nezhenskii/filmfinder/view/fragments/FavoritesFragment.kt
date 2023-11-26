package com.nezhenskii.filmfinder.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nezhenskii.filmfinder.view.rv_adapters.FilmListRecyclerAdapter
import com.nezhenskii.filmfinder.view.MainActivity
import com.nezhenskii.filmfinder.view.rv_adapters.TopSpacingItemDecoration
import com.nezhenskii.filmfinder.databinding.FragmentFavouritesBinding
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.utils.AnimationHelper
import com.nezhenskii.filmfinder.viewmodel.FavoritesFragmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding: FragmentFavouritesBinding
        get() = _binding!!

    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val compositeDisposable = CompositeDisposable()

    private val viewmodel by lazy {
        ViewModelProvider.NewInstanceFactory().create(FavoritesFragmentViewModel::class.java)
    }
    private var filmsDatabase = listOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            filmsAdapter.addItems(field)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filmObserver = viewmodel.filmsListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filmsDatabase = it.filter { it.isInFavourites }

            }
        compositeDisposable.add(filmObserver)

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 2)

        binding.favoritesRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        filmsAdapter.addItems(filmsDatabase)
    }

    override fun onDestroy() {
        _binding = null
        compositeDisposable.dispose()
        super.onDestroy()
    }
}