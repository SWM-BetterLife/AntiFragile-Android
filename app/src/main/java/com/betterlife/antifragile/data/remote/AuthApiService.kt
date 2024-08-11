package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthLoginResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 인증 API 엔드포인트를 정의하는 인터페이스
 */
interface AuthApiService {

    @POST("/auth/sign-up")
    suspend fun signUp(
        @Body request: AuthSignUpRequest
    ): Response<BaseResponse<Any?>>

    @POST("/auth/login")
    suspend fun login(
        @Body request: AuthLoginRequest
    ): Response<BaseResponse<AuthLoginResponse>>
}