package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthLoginResponse
import com.betterlife.antifragile.data.model.auth.response.AuthReIssueTokenResponse
import com.betterlife.antifragile.data.model.auth.response.AuthSignUpResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.remote.AuthApiService

class AuthRepository(
    private val authApiService: AuthApiService
) : BaseRepository() {

    suspend fun signUp(request: AuthSignUpRequest): BaseResponse<AuthSignUpResponse> {
        return safeApiCall {
            authApiService.signUp(request)
        }
    }

    suspend fun login(request: AuthLoginRequest): BaseResponse<AuthLoginResponse> {
        return safeApiCall {
            authApiService.login(request)
        }
    }

    suspend fun reIssueToken(
        request: AuthReIssueTokenRequest
    ): BaseResponse<AuthReIssueTokenResponse> {
        return safeApiCall {
            authApiService.reIssueToken(request)
        }
    }

}