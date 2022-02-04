package com.example.githubrepo_testtask.data.network.models

import com.google.gson.annotations.SerializedName

data class CommitResponse (

    @SerializedName("author")
    val author: AuthorResponse,

    @SerializedName("committer")
    var committer: Any? = null,

    @SerializedName("message")
    val message: String,

    @SerializedName("tree")
    var tree: Any? = null,

    @SerializedName("url")
    val url: String,

    @SerializedName("comment_count")
    val commentCount: Long,

    @SerializedName("verification")
    var verification: Any? = null

)