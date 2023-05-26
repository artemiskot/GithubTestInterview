package com.example.githubtestinterview

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.githubtestinterview.Entities.GithubContentAPI
import com.example.githubtestinterview.databinding.ActivityWebViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var retryButton: Button

    private lateinit var binding: ActivityWebViewBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        webView = binding.webView
        retryButton = binding.retryButton

        val fileUrl = intent.getStringExtra("file_url")
        loadFileContent(fileUrl)

        retryButton.setOnClickListener {
            loadFileContent(fileUrl)
        }
    }

    private fun loadFileContent(fileUrl: String?) {
        if (fileUrl != null) {
            showLoading(true)
            if (!isNetworkConnected()) {
                showLoading(false)
                showError("Отсутствует соединение с интернетом, подключитесь к сети и попробуйте снова")
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = githubContentAPI.getRepoContent(fileUrl)

                    if (response.isSuccessful) {
                        val content = response.body()

                        withContext(Dispatchers.Main) {
                            showLoading(false)
                            showError(null)

                            if (content != null && content.encoding == "base64") {
                                val decodedBytes = Base64.decode(content.content, Base64.DEFAULT)
                                val decodedContent = String(decodedBytes)
                                webView.loadData(decodedContent, "text/html", "UTF-8")
                            } else {
                                webView.loadUrl("$fileUrl/raw")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showLoading(false)
                            showError("Failed to load file: ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showError(e.message)
                    }
                }
            }
        } else {
            finish()
        }
    }

    private fun showError(errorMessage: String?) {
        if (errorMessage != null) {
            webView.visibility = View.GONE
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorTextView.text = errorMessage
        } else {
            webView.visibility = View.VISIBLE
            binding.errorLayout.visibility = View.GONE
        }
    }

    private fun isNetworkConnected(): Boolean {
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

