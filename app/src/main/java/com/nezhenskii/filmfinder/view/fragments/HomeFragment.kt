package com.nezhenskii.filmfinder.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nezhenskii.filmfinder.view.rv_adapters.FilmListRecyclerAdapter
import com.nezhenskii.filmfinder.view.MainActivity
import com.nezhenskii.filmfinder.view.rv_adapters.TopSpacingItemDecoration
import com.nezhenskii.filmfinder.databinding.FragmentHomeBinding
import com.nezhenskii.filmfinder.domain.Film
import com.nezhenskii.filmfinder.utils.AnimationHelper
import com.nezhenskii.filmfinder.viewmodel.HomeFragmentViewModel
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    private val viewmodel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }
    private var filmsDatabase = listOf<Film>()
    set(value) {
        if (field == value) return
        field = value
        filmsAdapter.addItems(field)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root,
            requireActivity(),
            1
        )

        initRecyclerView()
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    filmsAdapter.addItems(filmsDatabase)
                    return true
                }
                val result = filmsDatabase.filter {
                    it.title.lowercase(Locale.getDefault())
                        .contains(newText.lowercase(Locale.getDefault()))
                }
                filmsAdapter.addItems(result)
                return true
            }
        })
        viewmodel.filmsListLiveData.observe(viewLifecycleOwner) {
            filmsDatabase = it
        }
    }

    private fun initRecyclerView() {
        binding.mainRecycler.apply {
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
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}