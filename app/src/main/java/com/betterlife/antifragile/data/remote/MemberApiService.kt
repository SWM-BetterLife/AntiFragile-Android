package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.model.member.response.MemberExistenceResponse
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.model.member.response.NicknameDuplicateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/*
 * 맴버 API 엔드포인트를 정의하는 인터페이스
 */
interface MemberApiService {
    @GET("/members")
    suspend fun getMemberDetail(): Response<BaseResponse<MemberDetailResponse>>

    @Multipart
    @POST("/members/profile")
    suspend fun modifyProfile(
        @Part profileImgFile: MultipartBody.Part?,
        @Part("profileModifyRequest") request: MemberProfileModifyRequest
    ): Response<BaseResponse<MemberProfileModifyResponse>>

    @GET("/members/remain-recommend-number")
    suspend fun getRemainRecommendNumber(): Response<BaseResponse<MemberRemainNumberResponse>>

    @GET("/members/existence")
    suspend fun checkMemberExistence(
        @Query("email") email: String,
        @Query("loginType") loginType: LoginType
    ): Response<BaseResponse<MemberExistenceResponse>>

    @GET("/members/duplication-check")
    suspend fun checkNicknameExistence(
        @Query("nickname") nickname: String
    ): Response<BaseResponse<NicknameDuplicateResponse>>
}