package com.example.githubrepo_testtask.presentation.screens.details_page


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.githubrepo_testtask.R
import com.example.githubrepo_testtask.databinding.DetailsPageFragmentBinding
import com.example.githubrepo_testtask.presentation.adapters.ShaListAdapter
import com.example.githubrepo_testtask.presentation.factories.factory
import java.util.*

class DetailsPageFragment : Fragment() {

    private val detailsPageViewModel by viewModels<DetailsPageViewModel> { factory() }

    private var binding: DetailsPageFragmentBinding? = null

    private var adapter: ShaListAdapter? = null

    private val requireBinding: DetailsPageFragmentBinding
        get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            arguments?.let {
                val nodeId = it.getString("ID_ALBUM", "")
                detailsPageViewModel.loadCommit(nodeId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = DetailsPageFragmentBinding.inflate(layoutInflater, container, false)
        this.binding = viewBinding
        adapter = ShaListAdapter()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        detailsPageViewModel.uiStateChanges.observe(viewLifecycleOwner, { detailsUiState ->
            renderState(detailsUiState)
        })
    }

    private fun renderState(detailsUiState: DetailsUiState) {
        when {

            detailsUiState.error != null -> {
                isErrorLoading(detailsUiState, View.VISIBLE)
            }

            detailsUiState.repository != null && detailsUiState.commit == null-> {
                isLoadingInfo(detailsUiState, View.VISIBLE)
            }



            detailsUiState.repository != null && detailsUiState.commit != null -> {
                isLoadedInfo(detailsUiState, View.GONE)
            }
        }
    }

    private fun isLoadingInfo(detailsUiState: DetailsUiState, visibility: Int) {
        isErrorLoading(detailsUiState, View.GONE)
        with(requireBinding) {
            textViewFullNameRepository.text = detailsUiState.repository?.fullName
            textViewLogin.text = detailsUiState.repository?.login
            progressBarIsLoading.visibility = visibility
            Glide.with(requireContext())
                .load(detailsUiState.repository?.avatarUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder_error)
                .circleCrop()
                .into(imageOwner)
            textViewPreCommiterName.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
            textViewPreDate.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
            textViewPreMessage.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
            textViewListSha.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
            imageViewButtonBack.setOnClickListener {
                this@DetailsPageFragment.requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun isLoadedInfo(detailsUiState: DetailsUiState, visibility: Int) {
        isLoadingInfo(detailsUiState, visibility)
        isErrorLoading(detailsUiState, View.GONE)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter!!.submitList(detailsUiState.commit?.listParent)
        with(requireBinding) {
            textViewPreMessage.visibility = View.VISIBLE
            textViewPreDate.visibility = View.VISIBLE
            textViewPreCommiterName.visibility = View.VISIBLE
            textViewListSha.visibility = View.VISIBLE
            textViewMessage.text = detailsUiState.commit?.message
            textViewDate.text = calculatePeriod(detailsUiState.commit!!.date)
            textViewCommiterName.text = detailsUiState.commit.name
            recyclerViewSha.layoutManager = layoutManager
            recyclerViewSha.adapter =adapter
        }
    }

    private fun isErrorLoading(detailsUiState: DetailsUiState, visibility: Int) {
        with(requireBinding) {
            textViewPreMessage.visibility = View.GONE
            textViewPreDate.visibility = View.GONE
            textViewPreCommiterName.visibility = View.GONE
            textViewListSha.visibility = View.GONE
            progressBarIsLoading.visibility = View.GONE
            firstLineTextProblem.visibility = visibility
            secondLineTextProblem.visibility = visibility
            textViewTryAgain.visibility = visibility
            textViewTryAgain.setOnClickListener {
                detailsUiState.repository?.let { it1 -> detailsPageViewModel.loadCommit(it1.nodeId) }
            }
        }
    }

    private fun calculatePeriod(date: String): String {
        return date.removeRange(date.indexOf("T"), date.length).replace("-", ".")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}