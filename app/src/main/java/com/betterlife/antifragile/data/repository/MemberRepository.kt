package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.member.request.MemberPasswordModifyRequest
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.model.member.response.NicknameDuplicateResponse
import com.betterlife.antifragile.data.remote.MemberApiService
import okhttp3.MultipartBody

class MemberRepository(
    private val memberApiService: MemberApiService
) : BaseRepository() {

    suspend fun getMemberDetail(): BaseResponse<MemberDetailResponse> {
        return safeApiCall {
            memberApiService.getMemberDetail()
        }
    }

    suspend fun modifyProfile(
        profileImgFile: MultipartBody.Part?,
        request: MemberProfileModifyRequest
    ): BaseResponse<MemberProfileModifyResponse> {
        return safeApiCall {
            memberApiService.modifyProfile(profileImgFile, request)
        }
    }

    suspend fun getRemainRecommendNumber(): BaseResponse<MemberRemainNumberResponse> {
        return safeApiCall {
            memberApiService.getRemainRecommendNumber()
        }
    }

    suspend fun checkNicknameExistence(
        nickname: String
    ): BaseResponse<NicknameDuplicateResponse> {
        return safeApiCall {
            memberApiService.checkNicknameExistence(nickname)
        }
    }

    suspend fun modifyPassword(request: MemberPasswordModifyRequest): BaseResponse<Any?> {
        return safeApiCall {
            memberApiService.modifyPassword(request)
        }
    }
}