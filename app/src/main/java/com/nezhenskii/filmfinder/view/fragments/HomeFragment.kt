package com.nezhenskii.filmfinder.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nezhenskii.filmfinder.view.rv_adapters.FilmListRecyclerAdapter
import com.nezhenskii.filmfinder.view.MainActivity
import com.nezhenskii.filmfinder.view.rv_adapters.TopSpacingItemDecoration
import com.nezhenskii.filmfinder.databinding.FragmentHomeBinding
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.utils.AnimationHelper
import com.nezhenskii.filmfinder.viewmodel.HomeFragmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


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

    var isSearching = false
    private val compositeDisposable = CompositeDisposable()


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

        initSearchView()
        initPullToRefresh()
        initRecyclerView()
        initPreferencesListener()
        val filmDispose = viewmodel.filmsListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filmsDatabase = it
                isLoading = false
            }
        compositeDisposable.add(filmDispose)

        val pbDispose = viewmodel.showProgressBar
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.progressBar.isVisible = it
            }
        compositeDisposable.add(pbDispose)

        viewmodel.errorEvent.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initPreferencesListener() {
        viewmodel.interactor.registerListener { _, _ ->
            if (!isLoading) {
                //Очищаем базу данных
                viewmodel.clearDb()
                //Возвращаемся на первую страницу
                viewmodel.page = 1
                //ставим флаг, что запрашиваем загрузку элементов
                isLoading = true
                //Делаем новый запрос фильмов на сервер
                viewmodel.getFilms()
            }
        }
    }

    private fun initPullToRefresh() {
        //Вешаем слушатель, чтобы вызвался pull to refresh
        binding.pullToRefresh.setOnRefreshListener {
            //Убираем крутящееся колечко
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        val searchDispose = Observable.create { subscriber ->
            binding.searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText != "") isSearching = true
                    subscriber.onNext(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    subscriber.onNext(query)
                    return false
                }
            })
        }
            .subscribeOn(Schedulers.io())
            .map {
                it.lowercase(Locale.getDefault()).trim()
            }
            .debounce(1200, TimeUnit.MILLISECONDS)
            .filter {
                if (it.isBlank() && isSearching && !isLoading) {
                    isSearching = false
                    viewmodel.clearDb()
                    viewmodel.page = 1
                    isLoading = true
                    viewmodel.getFilms()
                }
                it.isNotBlank()
            }
            .flatMap {
                viewmodel.getSearchResult(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Toast.makeText(
                        requireContext(),
                        "Error during search query",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onNext = {
                    filmsAdapter.addItems(it)
                }
            )
        compositeDisposable.add(searchDispose)
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
            val decorator = TopSpacingItemDecoration(6)
            addItemDecoration(decorator)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager
                    //сколько элементов на экране
                    val visibleItemCount: Int = layoutManager.childCount
                    //сколько всего элементов
                    val totalItemCount: Int = layoutManager.itemCount
                    //какая позиция первого элемента
                    val firstVisibleItem =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    //смещение для более плавной прокрутки
                    val offset = 3
                    //проверяем, идет загрузка или нет
                    if (!isLoading && totalItemCount > 0) {
                        if (visibleItemCount + firstVisibleItem + offset >= totalItemCount) {
                            //ставим флаг, что запрашиваем загрузку элементов
                            isLoading = true
                            viewmodel.getFilms()
                        }
                    }
                }
            })
            filmsAdapter.addItems(filmsDatabase)
        }

    }

    override fun onDestroy() {
        _binding = null
        compositeDisposable.dispose()
        super.onDestroy()
    }
}