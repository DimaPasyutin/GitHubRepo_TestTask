package com.example.githubrepo_testtask.data.network.retrofit

import com.example.githubrepo_testtask.data.network.models.CommitGeneralInfoResponse
import com.example.githubrepo_testtask.data.network.models.RepositoryResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface RepoApi {

    @GET("repositories")
    @Headers("Accept:application/vnd.github.v3+json")
    fun getRepositories(@Query("since") count: Int): Single<List<RepositoryResponse>>

    @GET
    @Headers("Content-Type:application/json")
    fun getCommitInfo(@Url url: String): Single<List<CommitGeneralInfoResponse>>

}