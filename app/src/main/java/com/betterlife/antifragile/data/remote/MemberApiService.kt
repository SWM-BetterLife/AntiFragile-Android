package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/*
 * 맴버 API 엔드포인트를 정의하는 인터페이스
 */
interface MemberApiService {
    @GET("/members")
    suspend fun getMemberDetail(): Response<BaseResponse<MemberDetailResponse>>

    @Multipart
    @POST("/members/profile")
    suspend fun modifyProfile(
        @Part profileImgFile: MultipartBody.Part? = null,
        @Part("profileModifyRequest") profileModifyRequest: RequestBody
    ): Response<BaseResponse<MemberProfileModifyResponse>>

    @GET("/members/remain-recommend-number")
    suspend fun getRemainRecommendNumber(): Response<BaseResponse<MemberRemainNumberResponse>>
}