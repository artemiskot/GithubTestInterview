package com.example.githubtestinterview.Services

import com.example.githubtestinterview.Entities.RepoResponse
import com.example.githubtestinterview.Entities.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {
    @GET("/search/users")
    suspend fun searchUsers(@Query("q") query: String): UserResponse

    @GET("/search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): RepoResponse
}
