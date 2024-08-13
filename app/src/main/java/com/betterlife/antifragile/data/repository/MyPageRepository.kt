package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.auth.request.AuthLogoutRequest
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.member.response.MemberMyPageResponse
import com.betterlife.antifragile.data.remote.AuthApiService
import com.betterlife.antifragile.data.remote.MemberApiService

class MyPageRepository(
    private val memberApiService: MemberApiService,
    private val authApiService: AuthApiService
) : BaseRepository() {

    suspend fun getMemberMyPage(): BaseResponse<MemberMyPageResponse> {
        return safeApiCall {
            memberApiService.getMemberMyPage()
        }
    }

    suspend fun logout(request: AuthLogoutRequest): BaseResponse<Any?> {
        return safeApiCall {
            authApiService.logout(request)
        }
    }

    suspend fun delete(): BaseResponse<Any?> {
        return safeApiCall {
            authApiService.delete()
        }
    }
}