package com.example.githubrepo_testtask.domain.repository

import com.example.githubrepo_testtask.domain.models.Commit
import com.example.githubrepo_testtask.domain.models.Repository
import io.reactivex.Single

interface RepoRepository {

    fun loadRepositories(count: Int): Single<List<Repository>>

    fun loadCommitsInfo(commitUrl: String): Single<Commit>

    fun saveRepositoriesInMemoryCache(newRepo: List<Repository>)

}