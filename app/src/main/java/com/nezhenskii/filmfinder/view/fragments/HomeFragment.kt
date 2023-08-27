package com.nezhenskii.filmfinder.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    var isLoading = false

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
            isLoading = false
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

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager
                    //сколько элементов на экране
                    val visibleItemCount: Int = layoutManager.childCount
                    //сколько всего элементов
                    val totalItemCount: Int = layoutManager.itemCount
                    //какая позиция первого элемента
                    val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    //смещение для более плавной прокрутки
                    val offset = 3
                    //проверяем, идет загрузка или нет
                    if (!isLoading) {
                        if (visibleItemCount + firstVisibleItem + offset >= totalItemCount) {
                            //ставим флаг, что запрашиваем загрузку элементов
                            isLoading = true
                            viewmodel.getNextPage()
                        }
                    }
                }
            })
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}