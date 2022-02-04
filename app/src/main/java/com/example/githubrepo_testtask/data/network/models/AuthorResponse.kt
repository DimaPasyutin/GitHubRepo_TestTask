package com.example.githubrepo_testtask.data.network.models

import com.google.gson.annotations.SerializedName

data class AuthorResponse (

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("date")
    val date: String

)