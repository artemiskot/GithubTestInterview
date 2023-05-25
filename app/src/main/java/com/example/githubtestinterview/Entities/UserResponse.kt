package com.example.githubtestinterview.Entities

data class UserResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<User>
)

data class User(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val html_url: String,
    val score: Double
)