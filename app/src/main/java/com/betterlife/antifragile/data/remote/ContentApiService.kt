package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.content.response.ContentRecommendResponse
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

/*
 * 콘텐츠 API 엔드포인트를 정의하는 인터페이스
 */
interface ContentApiService {

    @GET("contents")
    suspend fun getContents(@Query("date") date: LocalDate): BaseResponse<ContentRecommendResponse>
}