package com.betterlife.antifragile.config

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.remote.AuthApiService
import com.betterlife.antifragile.presentation.ui.auth.AuthActivity
import com.betterlife.antifragile.presentation.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val authApiService: AuthApiService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken(context)
        val refreshToken = tokenManager.getRefreshToken(context)

        val requestBuilder = originalRequest.newBuilder()

        // Access token 추가
        if (!accessToken.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val response = chain.proceed(requestBuilder.build())

        // 토큰 만료 시 처리
        if (response.code == 401 || response.code == 403) {

            // RefreshToken 유효성 검사
            if (!refreshToken.isNullOrEmpty()) {
                response.close()

                // 토큰 재발급 요청
                val newTokens = runBlocking {
                    val reIssueResponse = authApiService.reIssueToken(
                        AuthReIssueTokenRequest(refreshToken)
                    )
                    if (reIssueResponse.isSuccessful) {
                        reIssueResponse.body()?.data
                    } else null
                }

                if (newTokens != null) {
                    // 토큰 갱신 후 재시도
                    tokenManager.saveTokens(context, newTokens.accessToken, newTokens.refreshToken)
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    // RefreshToken도 만료된 경우 - 로그아웃 처리
                    tokenManager.clearTokens(context)
                    showSessionExpiredMessage()
                    handleTokenExpiration()
                    throw IOException("Token re-issuance failed")
                }
            }
        }

        return response
    }

    private fun showSessionExpiredMessage() {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleTokenExpiration() {
        // 재로그인 로직 - 로그인 화면으로 이동
        val intent = Intent(context, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}