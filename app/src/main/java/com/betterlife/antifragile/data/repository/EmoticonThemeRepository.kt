package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonThemeEmoticonsResponse
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonsByEmotionResponse
import com.betterlife.antifragile.data.remote.EmoticonThemeApiService
import com.betterlife.antifragile.presentation.util.ApiErrorUtil.parseErrorResponse

/*
 * 감정티콘 테마 API 호출을 담당하는 리포지토리
 */
class EmoticonThemeRepository(private val emoticonThemeApiService: EmoticonThemeApiService) {

    suspend fun getEmoticonThemeEmoticons(
        emoticonThemeId: String
    ): BaseResponse<EmoticonThemeEmoticonsResponse> {

        return try {
            val response = emoticonThemeApiService.getEmoticonThemeEmoticons(emoticonThemeId)
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

    suspend fun getEmoticonByEmotion(
        emotion: String
    ): BaseResponse<EmoticonsByEmotionResponse> {

        return try {
            val response = emoticonThemeApiService.getEmoticonByEmotion(emotion)
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