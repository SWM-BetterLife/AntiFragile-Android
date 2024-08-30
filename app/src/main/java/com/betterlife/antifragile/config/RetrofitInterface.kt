package com.betterlife.antifragile.config

import android.content.Context
import com.betterlife.antifragile.data.remote.AuthApiService
import com.betterlife.antifragile.data.remote.ContentApiService
import com.betterlife.antifragile.data.remote.DiaryAnalysisApiService
import com.betterlife.antifragile.data.remote.EmoticonThemeApiService
import com.betterlife.antifragile.data.remote.LlmModelApiService
import com.betterlife.antifragile.data.remote.MemberApiService
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInterface {

    private var authApiService: AuthApiService

    init {
        authApiService = createAuthApiServiceWithoutInterceptor()
    }

    private fun getClient(
        context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        val authInterceptor = AuthInterceptor(context, TokenManager, authApiService)
        builder.addInterceptor(authInterceptor)

        return builder.build()
    }

    private fun createAuthApiServiceWithoutInterceptor(): AuthApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    private fun <T> createService(context: Context, serviceClass: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    fun createDiaryAnalysisApiService(context: Context): DiaryAnalysisApiService {
        return createService(context, DiaryAnalysisApiService::class.java)
    }

    fun createEmoticonThemeApiService(context: Context): EmoticonThemeApiService {
        return createService(context, EmoticonThemeApiService::class.java)
    }

    fun createContentApiService(context: Context): ContentApiService {
        return createService(context, ContentApiService::class.java)
    }

    fun createMemberApiService(context: Context): MemberApiService {
        return createService(context, MemberApiService::class.java)
    }

    fun createLlmModelApiService(context: Context): LlmModelApiService {
        return createService(context, LlmModelApiService::class.java)
    }

    fun getAuthApiService(): AuthApiService {
        return authApiService
    }
}
