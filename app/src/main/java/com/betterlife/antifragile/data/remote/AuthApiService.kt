package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthLoginResponse
import com.betterlife.antifragile.data.model.auth.response.AuthReIssueTokenResponse
import com.betterlife.antifragile.data.model.auth.response.AuthSignUpResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * 인증 API 엔드포인트를 정의하는 인터페이스
 */
interface AuthApiService {

    @Multipart
    @POST("/auth/sign-up")
    suspend fun signUp(
        @Part profileImgFile: MultipartBody.Part?,
        @Part("authSignUpRequest") request: AuthSignUpRequest
    ): Response<BaseResponse<AuthSignUpResponse>>

    @POST("/auth/login")
    suspend fun login(
        @Body request: AuthLoginRequest
    ): Response<BaseResponse<AuthLoginResponse>>

    @POST("/token/re-issuance")
    suspend fun reIssueToken(
        @Body request: AuthReIssueTokenRequest
    ): Response<BaseResponse<AuthReIssueTokenResponse>>
}