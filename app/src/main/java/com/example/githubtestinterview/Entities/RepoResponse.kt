package com.example.githubtestinterview.Entities

data class RepoResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<Repo>
)

data class Repo(
    val id: Int,
    val name: String,
    val full_name: String,
    val owner: Owner,
    val description: String?,
    val forks_count: Int,
    val score: Double
)

data class Owner(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val html_url: String,
)
