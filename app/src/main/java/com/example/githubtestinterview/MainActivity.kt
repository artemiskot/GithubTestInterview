package com.example.githubtestinterview

import CombinedAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubtestinterview.Services.GithubService
import com.example.githubtestinterview.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val githubService = retrofit.create(GithubService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.length >= 3) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun performSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val userSearchDeferred = async { githubService.searchUsers(query) }
            val repoSearchDeferred = async { githubService.searchRepositories(query) }

            val userSearchResults = userSearchDeferred.await()
            val repoSearchResults = repoSearchDeferred.await()

            val combinedResults = (userSearchResults.items + repoSearchResults.items)

            withContext(Dispatchers.Main) {
                binding.recyclerView.adapter = CombinedAdapter(combinedResults)
            }
        }
    }
}
