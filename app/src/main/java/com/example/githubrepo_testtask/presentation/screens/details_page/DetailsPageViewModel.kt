package com.example.githubrepo_testtask.presentation.screens.details_page

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubrepo_testtask.App
import com.example.githubrepo_testtask.domain.models.Commit
import com.example.githubrepo_testtask.domain.models.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DetailsPageViewModel(app: App): ViewModel() {

    private val uiState = MutableLiveData(DetailsUiState(null, null, null, false))
    val uiStateChanges: LiveData<DetailsUiState> = uiState

    private val currentUiState: DetailsUiState
        get() = requireNotNull(uiState.value)

    private val asyncRepositories = CompositeDisposable()

    private val repoRepository = app.repoRepository

    fun loadCommit(nodeId: String) {
        getRepository(nodeId)
    }

    private fun fetchCommit(commitUrl: String) {
        repoRepository.loadCommitsInfo(commitUrl)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onLoadSuccess, this::onLoadError)
            .addTo(asyncRepositories)
    }

    private fun getRepository(nodeId: String) {
        if (nodeId == "") {
            val newUiState = currentUiState.copy(
                repository = null
            )
            uiState.value = newUiState
        }

        val repositoryList = repoRepository.repoInMemoryCache.toList()
        val position = repoRepository.repoInMemoryCache.indexOfFirst { repository ->
            repository.nodeId == nodeId
        }

        if (position == -1) {
            val newUiState = currentUiState.copy(
                repository = null
            )
            uiState.value = newUiState
        } else {
            fetchCommit(repositoryList[position].commitsUrl)
            val newUiState = currentUiState.copy(
                repository = repositoryList[position],
                isLoading = true
            )
            uiState.value = newUiState
        }
    }

    private fun  onLoadSuccess(commit: Commit) {
        updateState {
            copy(
                commit = commit,
                error = null,
                isLoading = false
            )
        }
    }

    private fun onLoadError(throwable: Throwable) {
        Log.i("!!!", throwable.stackTraceToString())
        updateState {
            copy(
                commit = null,
                error = throwable,
                isLoading = false
            )
        }
    }

    private fun updateState(mutate: DetailsUiState.() -> DetailsUiState) {
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

data class DetailsUiState(
    val repository: Repository?,
    val commit: Commit?,
    val error: Throwable?,
    val isLoading: Boolean,
    )