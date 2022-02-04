package com.example.githubrepo_testtask.presentation.factories

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubrepo_testtask.App
import com.example.githubrepo_testtask.presentation.screens.details_page.DetailsPageViewModel
import com.example.githubrepo_testtask.presentation.screens.main_page.RepositoriesPageViewModel

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            RepositoriesPageViewModel::class.java -> {
                RepositoriesPageViewModel(app)
            }
            DetailsPageViewModel::class.java -> {
                DetailsPageViewModel(app)
            }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)