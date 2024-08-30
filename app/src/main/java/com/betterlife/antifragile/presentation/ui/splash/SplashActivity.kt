package com.betterlife.antifragile.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.presentation.ui.auth.AuthActivity
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.ModelDownloader
import com.betterlife.antifragile.presentation.util.TokenManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupProgressBar()
        setupViewModel()
        setupObserver()
        checkAndDownloadModel()
    }

    private fun setupProgressBar() {
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupViewModel() {
        val factory = SplashViewModelFactory(this)
        splashViewModel = factory.create(SplashViewModel::class.java)
    }

    private fun setupObserver() {
        splashViewModel.llmModelUrl.observe(this) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response.data?.modelUrl?.let { url ->
                        startModelDownload(url)
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(
                        this,
                        "모델 URL을 가져오지 못했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // do nothing
                }
            }
        }

        splashViewModel.reIssueToken.observe(this) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response.data?.let { newTokens ->
                        TokenManager.saveTokens(this, newTokens.accessToken, newTokens.refreshToken)
                        navigateToMainActivity()
                    }
                }
                Status.ERROR, Status.FAIL -> {
                    navigateToLogin()
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun checkAndDownloadModel() {
        val modelDownloader = ModelDownloader(this)

        if (modelDownloader.isModelAlreadyDownloaded()) {
            progressBar.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                autoLoginIfNeeded()
            }, 2000)
        } else {
            progressBar.visibility = View.VISIBLE
            splashViewModel.getLlmModelUrl()
        }
    }

    private fun startModelDownload(modelUrl: String) {
        val modelDownloader = ModelDownloader(this)
        val handler = Handler(Looper.getMainLooper())

        modelDownloader.downloadModel(
            modelUrl,
            onProgressUpdate = { progress ->
                handler.post {
                    progressBar.progress = progress
                }
            },
            onSuccess = {
                handler.post {
                    autoLoginIfNeeded()
                }
            },
            onFailure = {
                handler.post {
                    Toast.makeText(
                        this,
                        "모델 다운로드에 실패했습니다. 인터넷 연결을 확인해 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun autoLoginIfNeeded() {
        val refreshToken = TokenManager.getRefreshToken(this)

        if (!refreshToken.isNullOrEmpty()) {
            splashViewModel.reIssueToken(AuthReIssueTokenRequest(refreshToken))
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}