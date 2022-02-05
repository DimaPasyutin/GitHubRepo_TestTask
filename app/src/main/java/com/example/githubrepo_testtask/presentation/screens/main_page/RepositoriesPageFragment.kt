package com.example.githubrepo_testtask.presentation.screens.main_page

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubrepo_testtask.R
import com.example.githubrepo_testtask.databinding.RepositoriesPageFragmentBinding
import com.example.githubrepo_testtask.domain.models.Repository
import com.example.githubrepo_testtask.presentation.adapters.RepositoriesRecyclerViewAdapter
import com.example.githubrepo_testtask.presentation.factories.factory
import com.example.githubrepo_testtask.presentation.screens.details_page.DetailsPageFragment

const val REQUEST_INDEX = "request index"
const val FIRST_REQUEST = 0

class RepositoriesPageFragment : Fragment() {

    private val repositoriesPageViewModel by viewModels<RepositoriesPageViewModel> { factory() }

    private var binding: RepositoriesPageFragmentBinding? = null

    private var adapter: RepositoriesRecyclerViewAdapter? = null

    private val requireBinding: RepositoriesPageFragmentBinding
        get() = binding!!

    var lastIdRepo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            repositoriesPageViewModel.loadRepositories(FIRST_REQUEST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = RepositoriesPageFragmentBinding.inflate(layoutInflater, container, false)
        this.binding = viewBinding
        if (savedInstanceState != null) {
            lastIdRepo = savedInstanceState.getInt(REQUEST_INDEX, 0)
        }
        adapter = RepositoriesRecyclerViewAdapter( object: RepositoriesRecyclerViewAdapter.Listener {
            override fun onWorkerClick(repository: Repository) {
                navigateToDetailPage(repository)
            }

            override fun onUploadRepositories(lastId: Int) {
                repositoriesPageViewModel.fetchUploadRepositories(lastId)
                lastIdRepo = lastId
            }

        })
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        requireBinding.repositoriesRecyclerView.layoutManager = layoutManager
        requireBinding.repositoriesRecyclerView.adapter = adapter
        val itemAnimator = requireBinding.repositoriesRecyclerView.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
        repositoriesPageViewModel.uiStateChanges.observe(viewLifecycleOwner, { repositoriesUiState ->
            renderState(repositoriesUiState)
        })
    }

    private fun renderState(repositoriesUiState: RepositoriesUiState) {
        when {

            repositoriesUiState.isFirstLoading -> {
                requireBinding.shimmer.startShimmer()
            }

            repositoriesUiState.isUploading != null && repositoriesUiState.isUploading -> {
                requireBinding.uploadProgressBar.visibility = VISIBLE
            }

            repositoriesUiState.error == null && repositoriesUiState.repositories.isNotEmpty() && !repositoriesUiState.isUploadError -> {
                isLoadSuccess(repositoriesUiState)
            }

            repositoriesUiState.error != null && !repositoriesUiState.isUploadError && !repositoriesUiState.isRefreshed -> {
                isErrorLoading()
            }

            repositoriesUiState.error != null && repositoriesUiState.isUploadError -> {
                Toast.makeText(requireContext(), "Ошибка при загрузке", Toast.LENGTH_SHORT).show()
                adapter!!.repositories = repositoriesUiState.repositories
                requireBinding.uploadProgressBar.visibility = GONE
                requireBinding.shimmer.visibility = GONE
            }

            repositoriesUiState.isRefreshed -> {
                requireBinding.swipeContainer.isRefreshing = true
            }
        }
    }

    private fun isLoadSuccess(repositoriesUiState: RepositoriesUiState) {
        Log.i("!!!", repositoriesUiState.repositories[0].id.toString() + repositoriesUiState.repositories[repositoriesUiState.repositories.size - 1].id.toString())
        with(requireBinding) {
            uploadProgressBar.visibility = GONE
            repositoriesRecyclerView.visibility = VISIBLE
            adapter!!.repositories = repositoriesUiState.repositories
            spaceViewProblem.visibility = GONE
            firstLineTextProblem.visibility = GONE
            secondLineTextProblem.visibility = GONE
            textViewTryAgain.visibility = GONE
            shimmer.visibility = GONE
            shimmer.stopShimmer()
            swipeContainer.setOnRefreshListener {
                repositoriesPageViewModel.fetchRefreshingRepositories(FIRST_REQUEST)
            }
            swipeContainer.isRefreshing = false
        }
    }

    private fun isErrorLoading() {
        with(requireBinding) {
            uploadProgressBar.visibility = GONE
            repositoriesRecyclerView.visibility = GONE
            spaceViewProblem.visibility = VISIBLE
            firstLineTextProblem.visibility = VISIBLE
            secondLineTextProblem.visibility = VISIBLE
            textViewTryAgain.visibility = VISIBLE
            textViewTryAgain.setOnClickListener {
                repositoriesPageViewModel.loadRepositories(lastIdRepo)
            }
            shimmer.visibility = GONE
            swipeContainer.isRefreshing = false
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(REQUEST_INDEX, lastIdRepo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}