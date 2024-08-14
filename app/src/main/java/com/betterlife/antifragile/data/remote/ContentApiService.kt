package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.content.response.ContentDetailResponse
import com.betterlife.antifragile.data.model.content.response.ContentListResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

/*
 * 콘텐츠 API 엔드포인트를 정의하는 인터페이스
 */
interface ContentApiService {

    @POST("/contents")
    suspend fun recommendContents(
        @Query("date") date: String
    ): Response<BaseResponse<ContentListResponse>>

    @POST("/contents/re")
    suspend fun reRecommendContents(
        @Query("date") date: String,
        @Query("feedback") feedback: String
    ): Response<BaseResponse<ContentListResponse>>

    @POST("/contents/{contentId}/like")
    suspend fun likeContent(
        @Path("contentId") contentId: String
    ): Response<BaseResponse<Any?>>

    @DELETE("/contents/{contentId}/unlike")
    suspend fun unlikeContent(
        @Path("contentId") contentId: String
    ): Response<BaseResponse<Any?>>

    @GET("contents")
    suspend fun getContents(
        @Query("date") date: String
    ): Response<BaseResponse<ContentListResponse>>

    @GET("/contents/{contentId}")
    suspend fun getContentDetail(
        @Path("contentId") contentId: String
    ): Response<BaseResponse<ContentDetailResponse>>
}