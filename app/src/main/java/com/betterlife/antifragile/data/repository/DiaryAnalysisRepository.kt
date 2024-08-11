package com.betterlife.antifragile.data.repository

import android.annotation.SuppressLint
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryAnalysisDailyResponse
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryAnalysisEmoticonsResponse
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

    suspend fun updateDiaryAnalysis(
        date: String,
        request: DiaryAnalysisCreateRequest
    ): BaseResponse<Any?> {
        return safeApiCall {
            diaryAnalysisApiService.updateDiaryAnalysis(date, request)
        }
    }

    suspend fun getDailyDiaryAnalysis(
        date: String
    ): BaseResponse<DiaryAnalysisDailyResponse> {
        return safeApiCall {
            diaryAnalysisApiService.getDailyDiaryAnalysis(date)
        }
    }

    @SuppressLint("DefaultLocale")
    suspend fun getMonthlyEmoticons(
        year: Int, month: Int
    ): BaseResponse<DiaryAnalysisEmoticonsResponse> {
        val monthString = String.format("%04d-%02d", year, month)
        return safeApiCall {
            diaryAnalysisApiService.getMonthlyEmoticons(monthString)
        }
    }
}