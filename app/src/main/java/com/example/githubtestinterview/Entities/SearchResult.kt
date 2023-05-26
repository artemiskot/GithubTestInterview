package com.example.githubtestinterview.Entities

sealed class SearchResult {
    data class UserResult(val user: User) : SearchResult()
    data class RepoResult(val repo: Repo) : SearchResult()

    val sortKey: String
        get() = when (this) {
            is UserResult -> user.login
            is RepoResult -> repo.name
        }
}
