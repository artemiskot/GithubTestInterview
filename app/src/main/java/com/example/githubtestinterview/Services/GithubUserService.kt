package com.example.githubtestinterview.Services

import com.example.githubtestinterview.Entities.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubUserService {
    @GET("search/users")
    suspend fun searchUsers(@Query("q") query: String): UserResponse
}
