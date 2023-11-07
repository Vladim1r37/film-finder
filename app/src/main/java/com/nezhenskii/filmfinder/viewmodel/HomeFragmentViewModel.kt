package com.nezhenskii.filmfinder.viewmodel

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.nezhenskii.filmfinder.App
import com.nezhenskii.filmfinder.data.entity.Film
import com.nezhenskii.filmfinder.domain.Interactor
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData: LiveData<List<Film>>
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()
    val errorEvent = SingleLiveEvent<String>()
    var lastCallTime: Long
    var page = 1
    private val timeout = 600_000 // in milliseconds

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDb()
        lastCallTime = interactor.getLastCallTime()
        page = interactor.getCurrentPage()
        if (System.currentTimeMillis() - lastCallTime > timeout) {
            interactor.saveLastCallTime(System.currentTimeMillis())
            getFilms()
        }
        interactor.repo.filmsDatabase = filmsListLiveData
    }

    fun getFilms() {
        interactor.saveLastCallTime(System.currentTimeMillis())
        showProgressBar.postValue(true)
        interactor.getFilmsFromApi(page, object : ApiCallback {
            override fun onSuccess() {
                showProgressBar.postValue(false)
                page++
                interactor.saveCurrentPage(page)

            }

            override fun onFailure() {
                showProgressBar.postValue(false)
                errorEvent.postValue("Ошибка получения данных от сервера")
            }

        })
    }

    fun clearDb() = interactor.clearDb()

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(
                "SingleLiveEvent",
                "Multiple observers registered but only one will be notified of changes."
            )
        }
        // Наблюдение за внутренним MutableLiveData
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
}