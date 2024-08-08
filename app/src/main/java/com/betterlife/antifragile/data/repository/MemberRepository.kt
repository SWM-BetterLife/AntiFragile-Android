package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
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
        profileModifyRequest: RequestBody
    ): BaseResponse<MemberProfileModifyResponse> {
        return safeApiCall {
            memberApiService.modifyProfile(profileImgFile, profileModifyRequest)
        }
    }
}