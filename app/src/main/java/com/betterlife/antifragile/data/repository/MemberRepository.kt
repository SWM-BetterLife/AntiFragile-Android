package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.model.member.response.MemberExistenceResponse
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.model.member.response.NicknameDuplicateResponse
import com.betterlife.antifragile.data.remote.MemberApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    suspend fun checkMemberExistence(
        email: String, loginType: LoginType
    ): BaseResponse<MemberExistenceResponse> {
        return safeApiCall {
            memberApiService.checkMemberExistence(email, loginType)
        }
    }

    suspend fun checkNicknameExistence(
        nickname: String
    ): BaseResponse<NicknameDuplicateResponse> {
        return safeApiCall {
            memberApiService.checkNicknameExistence(nickname)
        }
    }
}