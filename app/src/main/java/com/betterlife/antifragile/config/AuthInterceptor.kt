package com.betterlife.antifragile.config

import okhttp3.Interceptor
import okhttp3.Response

/*
 * 요청에 Authorization 헤더를 추가하는 Interceptor
 */
class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}