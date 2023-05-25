package com.example.githubtestinterview

import CombinedAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubtestinterview.Services.GithubService
import com.example.githubtestinterview.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.util.Log


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val INTERNET_PERMISSION_REQUEST_CODE = 1

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
        // Проверка разрешения доступа к Интернету
        if (!isInternetPermissionGranted()) {
            // Разрешение доступа к Интернету уже предоставлено
            // Можно выполнять операции, требующие доступ к Интернету
        } else {
            // Разрешение доступа к Интернету не предоставлено
            // Запрос разрешения доступа к Интернету
            requestInternetPermission()
        }
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

    private fun isInternetPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestInternetPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            INTERNET_PERMISSION_REQUEST_CODE
        )
    }
    // Обработка результата запроса разрешения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение доступа к Интернету было предоставлено

            } else {
                // Разрешение доступа к Интернету было отклонено

            }
        }
    }

    private fun performSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MainActivity", "performSearch started with query: $query")

            val userSearchDeferred = async { githubService.searchUsers(query) }
            val repoSearchDeferred = async { githubService.searchRepositories(query) }

            val userSearchResults = userSearchDeferred.await()
            val repoSearchResults = repoSearchDeferred.await()

            Log.d("MainActivity", "userSearchResults: $userSearchResults")
            Log.d("MainActivity", "repoSearchResults: $repoSearchResults")

            val combinedResults = (userSearchResults.items + repoSearchResults.items)

            withContext(Dispatchers.Main) {
                val combinedResults = userSearchResults.items + repoSearchResults.items
                val adapter = CombinedAdapter(combinedResults)
                binding.recyclerView.adapter = adapter
            }
        }
    }
}