package com.example.githubrepo_testtask.data.network.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class CommitGeneralInfoResponse (

    @SerializedName("sha")
    val sha: String,

    @SerializedName("node_id")
    val nodeId: String,

    @SerializedName("commit")
    val commit: CommitResponse,

    @SerializedName("url")
    val url: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("comments_url")
    val commentsUrl: String,

    @SerializedName("author")
    var author: Any?,

    @SerializedName("committer")
    var committer: Any?,

    @SerializedName("parents")
    val parents: List<ParentResponse>

)
