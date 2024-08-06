package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.remote.DiaryAnalysisApiService
import com.betterlife.antifragile.presentation.util.ApiErrorUtil.parseErrorResponse

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
}