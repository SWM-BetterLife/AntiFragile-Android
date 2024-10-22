package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.auth.request.AuthReIssueTokenRequest
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthLoginResponse
import com.betterlife.antifragile.data.model.auth.response.AuthReIssueTokenResponse
import com.betterlife.antifragile.data.model.auth.response.AuthSignUpResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.member.response.MemberExistenceResponse
import com.betterlife.antifragile.data.remote.AuthApiService
import okhttp3.MultipartBody

class AuthRepository(
    private val authApiService: AuthApiService
) : BaseRepository() {

    suspend fun signUp(
        profileImgFile: MultipartBody.Part?, request: AuthSignUpRequest
    ): BaseResponse<AuthSignUpResponse> {
        return safeApiCall {
            authApiService.signUp(profileImgFile, request)
        }
    }

    suspend fun login(request: AuthLoginRequest): BaseResponse<AuthLoginResponse> {
        return safeApiCall {
            authApiService.login(request)
        }
    }

    suspend fun checkMemberExistence(
        email: String, loginType: LoginType
    ): BaseResponse<MemberExistenceResponse> {
        return safeApiCall {
            authApiService.checkMemberExistence(email, loginType)
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