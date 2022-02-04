package com.example.githubrepo_testtask.data.network.models

import com.google.gson.annotations.SerializedName

data class ParentResponse (

    @SerializedName("sha")
    val sha: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("html_url")
    val htmlUrl: String

)