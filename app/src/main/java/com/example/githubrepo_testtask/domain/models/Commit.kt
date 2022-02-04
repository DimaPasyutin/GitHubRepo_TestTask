package com.example.githubrepo_testtask.domain.models

import com.example.githubrepo_testtask.data.network.models.ParentResponse

data class Commit (
    val message: String,
    val name: String,
    val date: String,
    val listParent: List<ParentResponse>
        )