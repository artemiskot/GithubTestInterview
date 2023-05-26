package com.example.githubtestinterview.Entities

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GithubContentAPI {
    @GET
    suspend fun getRepoContents(@Url url: String): Response<List<Content>>

    @GET
    suspend fun getRepoContent(@Url url: String): Response<Content>
}



data class Content(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    val html_url: String,
    val git_url: String,
    val download_url: String?,
    val type: String,
    val content: String?,
    val encoding: String?
)



data class Links(
    val self: String,
    val git: String,
    val html: String
)