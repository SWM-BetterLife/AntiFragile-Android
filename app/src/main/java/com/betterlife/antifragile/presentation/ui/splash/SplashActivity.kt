package com.betterlife.antifragile.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.presentation.ui.auth.AuthActivity
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.ModelDownloader
import com.betterlife.antifragile.presentation.util.TokenManager
import kotlinx.coroutines.runBlocking

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)
        setupViewModel()
        setupObserver()
        checkAndDownloadModel()
    }

    private fun setupViewModel() {
        val factory = SplashViewModelFactory(this)
        splashViewModel = factory.create(SplashViewModel::class.java)
    }

    private fun setupObserver() {
        splashViewModel.llmModelUrl.observe(this) { response ->
            if (response.status == Status.SUCCESS) {
                response.data?.modelUrl?.let { url ->
                    startModelDownload(url)
                }
            } else {
                Toast.makeText(
                    this,
                    "모델 URL을 가져오지 못했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkAndDownloadModel() {
        val modelDownloader = ModelDownloader(this)

        if (modelDownloader.isModelAlreadyDownloaded()) {
            Handler(Looper.getMainLooper()).postDelayed({
                autoLoginIfNeeded()
            }, 2000)
        } else {
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
            // 토큰 유효성 확인
            checkTokenValidity(refreshToken)
        } else {
            // 토큰이 없으면 로그인 화면으로 이동
            navigateToLogin()
        }
    }

    private fun checkTokenValidity(refreshToken: String) {
        // API 호출 전 토큰 유효성을 확인하기 위해 서버에 간단한 요청을 보냄
        runBlocking {
            val authApiService = RetrofitInterface.getAuthApiService()
            val response = try {
                authApiService.reIssueToken(AuthReIssueTokenRequest(refreshToken))
            } catch (e: Exception) {
                null
            }

            if (response?.isSuccessful == true && response.body()?.status == Status.SUCCESS) {
                // 새 토큰 저장 및 메인 화면으로 이동
                val newTokens = response.body()?.data
                if (newTokens != null) {
                    TokenManager.saveTokens(this@SplashActivity, newTokens.accessToken, newTokens.refreshToken)
                    navigateToMainActivity()
                } else {
                    navigateToLogin()
                }
            } else {
                navigateToLogin()
            }
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