package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthLoginResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.remote.AuthApiService

class AuthRepository(
    private val authApiService: AuthApiService
) : BaseRepository() {

    suspend fun signUp(request: AuthSignUpRequest): BaseResponse<Any?> {
        return safeApiCall {
            authApiService.signUp(request)
        }
    }

    suspend fun login(request: AuthLoginRequest): BaseResponse<AuthLoginResponse> {
        return safeApiCall {
            authApiService.login(request)
        }
    }

}