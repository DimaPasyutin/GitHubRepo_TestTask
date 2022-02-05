package com.example.githubrepo_testtask.presentation.screens.main_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubrepo_testtask.App
import com.example.githubrepo_testtask.domain.models.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RepositoriesPageViewModel(app: App): ViewModel() {

    private val uiState = MutableLiveData(RepositoriesUiState(emptyList(), null, false, false, false, false))
    val uiStateChanges: LiveData<RepositoriesUiState> = uiState

    private val currentUiState: RepositoriesUiState
        get() = requireNotNull(uiState.value)

    private val asyncRepositories = CompositeDisposable()

    private val repoRepository = app.repoRepository

    private var oldRepositories = emptyList<Repository>()

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

    fun fetchUploadRepositories(count: Int) {
        updateState {
            copy(
                error = null,
                isFirstLoading = false,
                isUploading = true,
                isUploadError = false
            )
        }
        repoRepository.loadRepositories(count)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onUploadSuccess, this::onUploadError)
            .addTo(asyncRepositories)
    }

    fun fetchRefreshingRepositories(count: Int) {
        repoRepository.loadRepositories(count)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onRefreshSuccess, this::onRefreshingError)
            .addTo(asyncRepositories)
    }

    private fun  onLoadSuccess(repositories: List<Repository>) {
        oldRepositories = repositories
        updateState {
            copy(
                repositories = repositories,
                error = null,
                isFirstLoading = false,
                isUploading = false,
                isUploadError = false
            )
        }
    }

    private fun  onUploadSuccess(repositories: List<Repository>) {
        oldRepositories = repositories
        updateState {
            copy(
                repositories = repositories,
                error = null,
                isFirstLoading = false,
                isUploading = null,
                isUploadError = false
            )
        }
    }

    private fun  onRefreshSuccess(repositories: List<Repository>) {
        // not useful only for demonstrating refreshing
        oldRepositories = if (oldRepositories == repositories) {
            repositories.subList(2, repositories.size - 1)
        } else {
            repositories
        }
        updateState {
            copy(
                repositories = oldRepositories,
                error = null,
                isFirstLoading = false,
                isUploading = false,
                isUploadError = false,
                isRefreshed = true
            )
        }
    }

    private fun onLoadError(throwable: Throwable) {
        updateState {
            copy(
                error = throwable,
                isFirstLoading = false,
                isUploading = false,
                isUploadError = false,
                isRefreshed = false
            )
        }
    }

    private fun onUploadError(throwable: Throwable) {
        updateState {
            copy(
                error = throwable,
                isFirstLoading = false,
                isUploading = false,
                isUploadError = true
            )
        }
    }

    private fun onRefreshingError(throwable: Throwable) {
        updateState {
            copy(
                error = null,
                isFirstLoading = false,
                isUploading = false,
                isUploadError = false,
                isRefreshed = false
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
    val isUploading: Boolean?,
    val isUploadError: Boolean,
    val isRefreshed: Boolean

)