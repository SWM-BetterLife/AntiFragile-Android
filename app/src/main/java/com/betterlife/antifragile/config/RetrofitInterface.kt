package com.betterlife.antifragile.config

import com.betterlife.antifragile.data.remote.ContentApiService
import com.betterlife.antifragile.data.remote.DiaryAnalysisApiService
import com.betterlife.antifragile.data.remote.EmoticonThemeApiService
import com.betterlife.antifragile.data.remote.MemberApiService
import com.betterlife.antifragile.presentation.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInterface {

    // OkHttpClient 생성 함수
    private fun getClient(token: String?): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // 로깅 인터셉터 추가

        // 토큰이 제공된 경우 AuthInterceptor 추가
        token?.let {
            val authInterceptor = AuthInterceptor(it)
            builder.addInterceptor(authInterceptor)
        }

        return builder.build()
    }

    fun createDiaryAnalysisApiService(token: String): DiaryAnalysisApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getClient(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiaryAnalysisApiService::class.java)
    }

    fun createEmoticonThemeApiService(token: String): EmoticonThemeApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getClient(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmoticonThemeApiService::class.java)
    }

    fun createContentApiService(token: String): ContentApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getClient(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContentApiService::class.java)
    }

    fun createMemberApiService(token: String): MemberApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getClient(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MemberApiService::class.java)
    }
}