package com.example.githubtestinterview

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubtestinterview.Adapter.RepoContentAdapter
import com.example.githubtestinterview.Entities.GithubContentAPI
import com.example.githubtestinterview.Entities.Content
import com.example.githubtestinterview.databinding.ActivityRepoContentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class RepoContentActivity : AppCompatActivity() {

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val githubContentAPI = retrofit.create(GithubContentAPI::class.java)

    private lateinit var adapter: RepoContentAdapter

    private val folderStack = Stack<String>()

    private lateinit var binding: ActivityRepoContentBinding

    private lateinit var currentUrl: String

    private lateinit var repoUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoContentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        repoUrl = intent.getStringExtra("repo_url").toString()
        currentUrl = intent.getStringExtra("repo_url").toString()
        println("repoUrl "+repoUrl)

        folderStack.push("")
        if (repoUrl != null) {
            loadRepoContent(repoUrl)
        } else {
            finish()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.repoContentRecyclerView)
        adapter = RepoContentAdapter(
            onFolderClick = { path ->
                setCurrentUrl(path, true)
                loadRepoContent(currentUrl)
            },
            onFileClick = { path ->
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("file_url", "$repoUrl/$path")
                startActivity(intent)
            }
        )
        binding.retryButton.setOnClickListener {
            loadRepoContent(currentUrl)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        if (folderStack.size > 1) {
            val parentUrl = getParentUrl()
            loadRepoContent(parentUrl)
        } else if (folderStack.size == 1) {
            super.onBackPressed()
        }
    }

    private fun loadRepoContent(url: String) {
        currentUrl = url
        Log.d("RepoContent", "Loading URL: $url")
        showLoading(true)
        if (!isNetworkConnected()) {
            showLoading(false)
            showError("Отсутствует соединение с интернетом, подключитесь к сети и попробуйте снова")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = githubContentAPI.getRepoContents(url)
                if (response.isSuccessful) {
                    val content = response.body()
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showError(null)
                        if (content != null) {
                            val folders = mutableListOf<Content>()
                            val files = mutableListOf<Content>()
                            for (item in content) {
                                if (item.type == "dir") {
                                    folders.add(item)
                                } else {
                                    files.add(item)
                                }
                            }
                            val combinedList = ArrayList<Content>()
                            combinedList.addAll(folders)
                            combinedList.addAll(files)

                            adapter.setContents(combinedList)
                        } else {
                            showError("Failed to load content")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        if (response.code() == 403) {
                            showError("You have reached GitHub API rate limit. Please try again later.")
                        } else {
                            showError("Failed to load content: ${response.message()}")
                        }
                    }
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
            binding.repoContentRecyclerView.visibility = View.GONE
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorTextView.text = errorMessage
        } else {
            binding.errorLayout.visibility = View.GONE
            binding.repoContentRecyclerView.visibility = View.VISIBLE
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

    fun setCurrentUrl(path: String, addToStack: Boolean = true) {
        currentUrl = "$repoUrl/$path"
        if (addToStack) {
            folderStack.push(path)
        }
    }

    fun getParentUrl(): String {
        if (folderStack.size > 1) {
            folderStack.pop()
            val parentPath = folderStack.peek()
            return "$repoUrl/$parentPath"
        }
        return repoUrl
    }

}
