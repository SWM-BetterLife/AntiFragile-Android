package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.auth.AuthSignUpRequest
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
}