package com.example.githubrepo_testtask.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepo_testtask.R
import com.example.githubrepo_testtask.data.network.models.ParentResponse
import com.example.githubrepo_testtask.databinding.ItemShaBinding

class ShaListAdapter(): ListAdapter<ParentResponse, ShaViewHolder>(ShaDiffer()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemShaBinding.bind(layoutInflater.inflate(R.layout.item_sha, parent, false))
        return ShaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShaViewHolder, position: Int) {
        val parent = currentList[position]
        holder.bindSha(parent)
    }

}

class ShaViewHolder(
    private val bindingSha: ItemShaBinding
) : RecyclerView.ViewHolder(bindingSha.root) {

    fun bindSha(parentResponse: ParentResponse) {
        bindingSha.textViewSha.text = parentResponse.sha
    }
}

class ShaDiffer : DiffUtil.ItemCallback<ParentResponse>() {
    override fun areItemsTheSame(oldItem: ParentResponse, newItem: ParentResponse): Boolean {
        return oldItem.sha == newItem.sha
    }

    override fun areContentsTheSame(oldItem: ParentResponse, newItem: ParentResponse): Boolean {
        return oldItem == newItem
    }
}