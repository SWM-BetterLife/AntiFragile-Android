package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.remote.DiaryAnalysisApiService

/*
 * 일기 분석 API 호출을 담당하는 리포지토리
 */
class DiaryAnalysisRepository(private val diaryAnalysisApiService: DiaryAnalysisApiService) {

    suspend fun saveDiaryAnalysis(request: DiaryAnalysisCreateRequest): BaseResponse<Any?> {
        return diaryAnalysisApiService.saveDiaryAnalysis(request)
    }

    // TODO: 월별 감정티콘 조회 API 호출
}