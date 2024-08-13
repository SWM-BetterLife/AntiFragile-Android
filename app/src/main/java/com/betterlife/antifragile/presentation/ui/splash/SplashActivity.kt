package com.betterlife.antifragile.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.presentation.ui.auth.AuthActivity
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.TokenManager
import kotlinx.coroutines.runBlocking

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)

        simulateInstallation()
    }

    private fun simulateInstallation() {
        // ProgressBar를 100%로 업데이트하기 위한 타이머 설정
        val handler = Handler(Looper.getMainLooper())
        var progressStatus = 0

        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                handler.post {
                    progressBar.progress = progressStatus
                }
                try {
                    // ProgressBar가 100%에 도달할 때까지 약간의 지연
                    Thread.sleep(40)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            // 설치가 완료되면 자동 로그인 체크를 시작
            handler.post {
                autoLoginIfNeeded()
            }
        }.start()
    }

    private fun autoLoginIfNeeded() {
        val accessToken = TokenManager.getAccessToken(this)
        val refreshToken = TokenManager.getRefreshToken(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                // 토큰 유효성 확인
                checkTokenValidity(accessToken, refreshToken)
            } else {
                // 토큰이 없으면 로그인 화면으로 이동
                navigateToLogin()
            }
        }, 2000)
    }

    private fun checkTokenValidity(accessToken: String, refreshToken: String) {
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