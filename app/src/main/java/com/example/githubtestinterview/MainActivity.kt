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
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import com.example.githubtestinterview.Entities.SearchResult


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
                binding.retryButton.setOnClickListener {
                    showError(null)
                    performSearch(query)
                }

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
        showLoading(true)
        if (!isNetworkConnected()) {
            showLoading(false)
            showError("Отсутствует соединение с интернетом, подключитесь к сети и попробуйте снова")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MainActivity", "performSearch started with query: $query")

                val userSearchDeferred = async { githubService.searchUsers(query) }
                val repoSearchDeferred = async { githubService.searchRepositories(query) }

                val userSearchResults = userSearchDeferred.await()
                val repoSearchResults = repoSearchDeferred.await()

                val combinedResults = userSearchResults.items.map { SearchResult.UserResult(it) } +
                        repoSearchResults.items.map { SearchResult.RepoResult(it) }

                // Sort the results
                val sortedResults = combinedResults.sortedBy { it.sortKey }

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showError(null)

                    val adapter = CombinedAdapter(sortedResults)
                    binding.recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showError(e.message)
                }
            }
        }
    }

    private fun showError(errorMessage: String?) {
        if (errorMessage != null) {
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorTextView.text = errorMessage
            binding.recyclerView.visibility = View.GONE
            binding.searchView.visibility = View.GONE
        } else {
            binding.errorLayout.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.searchView.visibility = View.VISIBLE
        }
    }
    fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    private fun showLoading(show: Boolean) {
        if (show) {
            binding.loadingProgressBar.visibility = View.VISIBLE
        } else {
            binding.loadingProgressBar.visibility = View.GONE
        }
    }

}