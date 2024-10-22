package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.model.member.response.MemberMyPageResponse
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.model.member.response.NicknameDuplicateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
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
    suspend fun getMemberMyPage(): Response<BaseResponse<MemberMyPageResponse>>

    @GET("/members/detail")
    suspend fun getMemberDetail(): Response<BaseResponse<MemberDetailResponse>>

    @Multipart
    @POST("/members/profile")
    suspend fun modifyProfile(
        @Part profileImgFile: MultipartBody.Part?,
        @Part("profileModifyRequest") request: MemberProfileModifyRequest
    ): Response<BaseResponse<MemberProfileModifyResponse>>

    @GET("/members/remain-recommend-number")
    suspend fun getRemainRecommendNumber(): Response<BaseResponse<MemberRemainNumberResponse>>

    @GET("/members/duplication-check")
    suspend fun checkNicknameExistence(
        @Query("nickname") nickname: String
    ): Response<BaseResponse<NicknameDuplicateResponse>>

    @GET("/members/duplication-check")
    suspend fun checkMemberStatus(
        @Query("nickname") nickname: String
    ): Response<BaseResponse<NicknameDuplicateResponse>>

    @DELETE("/auth")
    suspend fun delete(): Response<BaseResponse<Any?>>
}