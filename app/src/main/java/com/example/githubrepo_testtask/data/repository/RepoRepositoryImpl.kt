package com.example.githubrepo_testtask.data.repository

import com.example.githubrepo_testtask.data.network.models.CommitGeneralInfoResponse
import com.example.githubrepo_testtask.data.network.models.RepositoryResponse
import com.example.githubrepo_testtask.data.network.retrofit.RepoApi
import com.example.githubrepo_testtask.domain.models.Commit
import com.example.githubrepo_testtask.domain.models.Repository
import com.example.githubrepo_testtask.domain.repository.RepoRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RepoRepositoryImpl(private val repoApi: RepoApi): RepoRepository {

    @Volatile
    var repoInMemoryCache: List<Repository> = mutableListOf()

    override fun loadRepositories(count: Int): Single<List<Repository>> {
        return repoApi.getRepositories(count)
            .map {mapToRepo(it)}
            .doOnSuccess { saveRepositoriesInMemoryCache(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun loadCommitsInfo(commitUrl: String): Single<Commit> {
        return repoApi.getCommitInfo(commitUrl)
            .map {mapToCommit(it)}
            .subscribeOn(Schedulers.io())
    }

    private fun mapToCommit(commitGeneralInfoResponse: List<CommitGeneralInfoResponse>): Commit  {
        val commitInfo = commitGeneralInfoResponse[0]
        return Commit(
            message = commitInfo.commit.message,
            name = commitInfo.commit.author.name,
            date = commitInfo.commit.author.date,
            listParent = commitInfo.parents
        )
    }

    @Synchronized
    override fun saveRepositoriesInMemoryCache(newRepo: List<Repository>) {
        if (repoInMemoryCache == newRepo) return
        repoInMemoryCache = newRepo
    }

    private fun mapToRepo(repositoryResponse: List<RepositoryResponse>): List<Repository> {
        val listRepositories = mutableListOf<Repository>()
        for (repo in repositoryResponse) {
            with(repo) {
                listRepositories.add(Repository(
                    nodeId = repo.nodeId,
                    id = id.toInt(),
                    fullName = fullName,
                    avatarUrl = ownerResponse.avatarUrl,
                    login = ownerResponse.login,
                    commitsUrl = commitsUrl.replace("{/sha}", "")
                ))
            }
        }
        if (repoInMemoryCache == listRepositories) return repoInMemoryCache
        return repoInMemoryCache + listRepositories
    }

}