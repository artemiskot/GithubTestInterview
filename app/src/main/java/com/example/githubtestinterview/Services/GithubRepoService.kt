package com.example.githubtestinterview.Services

import com.example.githubtestinterview.Entities.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubRepoService {
    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): RepoResponse
}