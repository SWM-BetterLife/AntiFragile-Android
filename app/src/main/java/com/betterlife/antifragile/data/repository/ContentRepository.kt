package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.content.response.ContentDetailResponse
import com.betterlife.antifragile.data.model.content.response.ContentListResponse
import com.betterlife.antifragile.data.remote.ContentApiService
import java.time.LocalDate

class ContentRepository(
    private val contentApiService: ContentApiService
) : BaseRepository() {

    suspend fun getRecommendContents(
        date: LocalDate
    ): BaseResponse<ContentListResponse> {
        return safeApiCall {
            contentApiService.recommendContents(date)
        }
    }

    suspend fun getReRecommendContents(
        date: LocalDate,
        feedback: String
    ): BaseResponse<ContentListResponse> {
        return safeApiCall {
            contentApiService.reRecommendContents(date, feedback)
        }
    }

    suspend fun getContents(
        date: LocalDate
    ): BaseResponse<ContentListResponse> {
        return safeApiCall {
            contentApiService.getContents(date)
        }
    }

    suspend fun getContentDetail(
        contentId: String
    ): BaseResponse<ContentDetailResponse> {
        return safeApiCall {
            contentApiService.getContentDetail(contentId)
        }
    }
}