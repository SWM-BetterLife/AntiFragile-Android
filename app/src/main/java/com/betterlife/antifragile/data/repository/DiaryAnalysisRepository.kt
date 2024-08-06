package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryAnalysisDailyResponse
import com.betterlife.antifragile.data.remote.DiaryAnalysisApiService

class DiaryAnalysisRepository(
    private val diaryAnalysisApiService: DiaryAnalysisApiService
) : BaseRepository() {

    suspend fun saveDiaryAnalysis(
        request: DiaryAnalysisCreateRequest
    ): BaseResponse<Any?> {
        return safeApiCall {
            diaryAnalysisApiService.saveDiaryAnalysis(request)
        }
    }

    suspend fun getDailyDiaryAnalysis(
        date: String
    ): BaseResponse<DiaryAnalysisDailyResponse> {
        return safeApiCall {
            diaryAnalysisApiService.getDailyDiaryAnalysis(date)
        }
    }
}