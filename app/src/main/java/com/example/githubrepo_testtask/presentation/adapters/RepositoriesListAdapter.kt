package com.example.githubrepo_testtask.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.githubrepo_testtask.R
import com.example.githubrepo_testtask.databinding.ItemRepositoryBinding
import com.example.githubrepo_testtask.domain.models.Repository

class RepositoriesListAdapter(private val listener: Listener): ListAdapter<Repository, RepositoryViewHolder>(RepoDiffer()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRepositoryBinding.bind(layoutInflater.inflate(R.layout.item_repository, parent, false))
        return RepositoryViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = currentList[position]
        holder.bindRepo(repository)
    }

    fun interface Listener {
        fun onWorkerClick(repository: Repository)
    }

}

class RepositoryViewHolder(
    private val bindingRepo: ItemRepositoryBinding,
    private val listener: RepositoriesListAdapter.Listener
) : RecyclerView.ViewHolder(bindingRepo.root) {

    fun bindRepo(repository: Repository) {
        with(bindingRepo) {
            textViewLoginOwner.text = repository.fullName
            textViewNameRepository.text = repository.login
            Glide.with(imageViewRepository.context)
                .load(repository.avatarUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder_error)
                .circleCrop()
                .into(imageViewRepository)
        }
        itemView.setOnClickListener { listener.onWorkerClick(repository) }
    }
}

class RepoDiffer : DiffUtil.ItemCallback<Repository>() {
    override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
        return oldItem.login == newItem.login
    }

    override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
        return oldItem == newItem
    }
}