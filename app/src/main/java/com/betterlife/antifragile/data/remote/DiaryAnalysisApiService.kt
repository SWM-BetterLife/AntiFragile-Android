package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryAnalysisDailyResponse
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryAnalysisEmoticonsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/*
 * 일기 분석 API 엔드포인트를 정의하는 인터페이스
 */
interface DiaryAnalysisApiService {
    @POST("/diary-analyses")
    suspend fun saveDiaryAnalysis(
        @Body request: DiaryAnalysisCreateRequest
    ): Response<BaseResponse<Any?>>

    @PATCH("/diary-analyses")
    suspend fun updateDiaryAnalysis(
        @Query("date") date: String,
        @Body request: DiaryAnalysisCreateRequest
    ): Response<BaseResponse<Any?>>

    @GET("/diary-analyses/emoticons")
    suspend fun getMonthlyEmoticons(
        @Query("year-month") yearMonth: String
    ): Response<BaseResponse<DiaryAnalysisEmoticonsResponse>>

    @GET("/diary-analyses/emotions")
    suspend fun getDailyDiaryAnalysis(
        @Query("date") date: String
    ): Response<BaseResponse<DiaryAnalysisDailyResponse>>
}