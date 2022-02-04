package com.example.githubrepo_testtask

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import com.example.githubrepo_testtask.data.network.retrofit.RepoApi
import com.example.githubrepo_testtask.data.repository.RepoRepositoryImpl
import com.example.githubrepo_testtask.domain.models.Repository
import com.example.githubrepo_testtask.domain.repository.RepoRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class App: Application() {

    lateinit var repoApi: RepoApi

    lateinit var repoRepository: RepoRepositoryImpl

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        configureRetrofit()
        repoRepository = RepoRepositoryImpl(repoApi)
    }

    private fun configureRetrofit() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        repoApi = retrofit.create(RepoApi::class.java)
    }


}