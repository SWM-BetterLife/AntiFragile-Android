package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.remote.DiaryAnalysisApiService
import com.betterlife.antifragile.presentation.util.ApiErrorUtil.parseErrorResponse

/*
 * 일기 분석 API 호출을 담당하는 리포지토리
 */
class DiaryAnalysisRepository(private val diaryAnalysisApiService: DiaryAnalysisApiService) {

    suspend fun saveDiaryAnalysis(request: DiaryAnalysisCreateRequest): BaseResponse<Any?> {
        return try {
            val response = diaryAnalysisApiService.saveDiaryAnalysis(request)
            if (response.isSuccessful) {
                response.body() ?: BaseResponse(Status.ERROR, "Unknown error", null)
            } else {
                val errorResponse = response.errorBody()?.string()
                val baseResponse = parseErrorResponse(errorResponse)
                BaseResponse(baseResponse.status, baseResponse.errorMessage, null)

            }
        } catch (e: Exception) {
            BaseResponse(Status.ERROR, e.message, null)
        }
    }
}