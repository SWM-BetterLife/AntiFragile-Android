package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import retrofit2.http.Body
import retrofit2.http.POST

/*
 * 일기 분석 API 엔드포인트를 정의하는 인터페이스
 */
interface DiaryAnalysisApiService {
    @POST("/diary-analyses")
    suspend fun saveDiaryAnalysis(@Body request: DiaryAnalysisCreateRequest): BaseResponse<Any?>
}