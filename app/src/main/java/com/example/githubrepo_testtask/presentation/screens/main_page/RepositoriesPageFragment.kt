package com.example.githubrepo_testtask.presentation.screens.main_page

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubrepo_testtask.R
import com.example.githubrepo_testtask.databinding.RepositoriesPageFragmentBinding
import com.example.githubrepo_testtask.domain.models.Repository
import com.example.githubrepo_testtask.presentation.adapters.RepositoriesListAdapter
import com.example.githubrepo_testtask.presentation.factories.factory
import com.example.githubrepo_testtask.presentation.screens.details_page.DetailsPageFragment

class RepositoriesPageFragment : Fragment() {

    private val repositoriesPageViewModel by viewModels<RepositoriesPageViewModel> { factory() }

    private var binding: RepositoriesPageFragmentBinding? = null

    private var adapter: RepositoriesListAdapter? = null

    private val requireBinding: RepositoriesPageFragmentBinding
        get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            repositoriesPageViewModel.loadRepositories(10)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = RepositoriesPageFragmentBinding.inflate(layoutInflater, container, false)
        this.binding = viewBinding
        adapter = RepositoriesListAdapter { repository -> navigateToDetailPage(repository) }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repositoriesPageViewModel.uiStateChanges.observe(viewLifecycleOwner, { repositoriesUiState ->
            renderState(repositoriesUiState)
        })
    }

    private fun renderState(repositoriesUiState: RepositoriesUiState) {
        when {

            repositoriesUiState.isFirstLoading -> {
                requireBinding.firstLoadProgressBar.visibility = View.VISIBLE
                isErrorLoading(View.GONE)
            }

            repositoriesUiState.error == null && repositoriesUiState.repositories.isNotEmpty() -> {
                isLoadSuccess(repositoriesUiState, View.GONE)
            }

            repositoriesUiState.error != null -> {
                requireBinding.firstLoadProgressBar.visibility = View.GONE
                isErrorLoading(View.VISIBLE)
            }
        }
    }

    private fun isLoadSuccess(repositoriesUiState: RepositoriesUiState, visibility: Int) {
        requireBinding.firstLoadProgressBar.visibility = visibility
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        requireBinding.repositoriesRecyclerView.layoutManager = layoutManager
        adapter!!.submitList(repositoriesUiState.repositories)
        requireBinding.repositoriesRecyclerView.adapter = adapter
        isErrorLoading(visibility)
    }

    private fun isErrorLoading(visibility: Int) {
        with(requireBinding) {
            spaceViewProblem.visibility= visibility
            firstLine.visibility= visibility
            secondLine.visibility= visibility
            textViewTryAgain.visibility= visibility
            textViewTryAgain.setOnClickListener {
                repositoriesPageViewModel.loadRepositories(10)
            }
        }
    }

    private fun navigateToDetailPage(repository: Repository) {
        val fragment = DetailsPageFragment().apply {
            arguments = Bundle().apply {
                putString("ID_ALBUM", repository.nodeId)
            }
        }
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}