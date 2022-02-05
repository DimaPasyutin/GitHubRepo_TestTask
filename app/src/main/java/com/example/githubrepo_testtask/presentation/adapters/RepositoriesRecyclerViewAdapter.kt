package com.example.githubrepo_testtask.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubrepo_testtask.R
import com.example.githubrepo_testtask.databinding.ItemRepositoryBinding
import com.example.githubrepo_testtask.domain.models.Repository

class RepositoriesRecyclerViewAdapter(private val listener: Listener): RecyclerView.Adapter<RepositoryViewHolder>(), View.OnClickListener {

    var repositories: List<Repository> = emptyList()
       set(newValue) {
           val diffCallback = RepoDiffCallback(field, newValue)
           val diffResult = DiffUtil.calculateDiff(diffCallback)
           field = newValue
           diffResult.dispatchUpdatesTo(this)
       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRepositoryBinding.bind(layoutInflater.inflate(R.layout.item_repository, parent, false))
        binding.root.setOnClickListener(this)
        return RepositoryViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = repositories[position]
        holder.bindRepo(repository, (repositories.size - 1) == position)
        holder.itemView.tag = repository
    }

    override fun getItemCount(): Int = repositories.size

    interface Listener {
        fun onWorkerClick(repository: Repository)

        fun onUploadRepositories(lastId: Int)
    }

    override fun onClick(v: View) {
        val repo = v.tag as Repository
        listener.onWorkerClick(repo)
    }

}

class RepositoryViewHolder(
    private val bindingRepo: ItemRepositoryBinding,
    private val listener: RepositoriesRecyclerViewAdapter.Listener
) : RecyclerView.ViewHolder(bindingRepo.root) {

    fun bindRepo(repository: Repository, needUpload: Boolean) {
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
        if (needUpload) listener.onUploadRepositories(repository.id)
    }
}

class RepoDiffCallback(
    private val oldList: List<Repository>,
    private val newList: List<Repository>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRepo = oldList[oldItemPosition]
        val newRepo = newList[newItemPosition]
        return oldRepo.id == newRepo.id && oldRepo.nodeId == newRepo.nodeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRepo = oldList[oldItemPosition]
        val newRepo = newList[newItemPosition]
        return oldRepo == newRepo
    }
}