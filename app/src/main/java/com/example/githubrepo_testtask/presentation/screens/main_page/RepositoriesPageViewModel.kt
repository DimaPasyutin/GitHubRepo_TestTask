package com.example.githubrepo_testtask.presentation.screens.main_page

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubrepo_testtask.App
import com.example.githubrepo_testtask.domain.models.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RepositoriesPageViewModel(app: App): ViewModel() {

    private val uiState = MutableLiveData(RepositoriesUiState(emptyList(), null, false, false))
    val uiStateChanges: LiveData<RepositoriesUiState> = uiState

    private val currentUiState: RepositoriesUiState
        get() = requireNotNull(uiState.value)

    private val asyncRepositories = CompositeDisposable()

    private val repoRepository = app.repoRepository

    fun loadRepositories(count: Int) {
        onStartLoading()
        fetchRepositories(count)
    }

    private fun fetchRepositories(count: Int) {
        repoRepository.loadRepositories(count)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onLoadSuccess, this::onLoadError)
            .addTo(asyncRepositories)
    }

    private fun  onLoadSuccess(repositories: List<Repository>) {
        updateState {
            copy(
                repositories = repositories,
                error = null,
                isFirstLoading = false,
                isUploading = false
            )
        }
    }

    private fun onLoadError(throwable: Throwable) {
        val repositories = emptyList<Repository>()
        updateState {
            copy(
                repositories = repositories,
                error = throwable,
                isFirstLoading = false,
                isUploading = false
            )
        }
    }

    private fun onStartLoading() {
        updateState {
            copy(
                isFirstLoading = true
            )
        }
    }

    private fun updateState(mutate: RepositoriesUiState.() -> RepositoriesUiState) {
        val newState = mutate(currentUiState)
        if (newState != currentUiState) {
            uiState.value = newState
        }
    }

    override fun onCleared() {
        asyncRepositories.clear()
    }

    private fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

}

data class RepositoriesUiState(
    val repositories: List<Repository>,
    val error: Throwable?,
    val isFirstLoading: Boolean,
    val isUploading: Boolean,

)