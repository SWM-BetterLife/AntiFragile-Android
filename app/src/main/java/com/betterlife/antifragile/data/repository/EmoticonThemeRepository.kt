package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonThemeEmoticonsResponse
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonsByEmotionResponse
import com.betterlife.antifragile.data.remote.EmoticonThemeApiService

class EmoticonThemeRepository(
    private val emoticonThemeApiService: EmoticonThemeApiService
) : BaseRepository() {

    suspend fun getEmoticonThemeEmoticons(
        emoticonThemeId: String
    ): BaseResponse<EmoticonThemeEmoticonsResponse> {
        return safeApiCall {
            emoticonThemeApiService.getEmoticonThemeEmoticons(emoticonThemeId)
        }
    }

    suspend fun getEmoticonByEmotion(
        emotion: String
    ): BaseResponse<EmoticonsByEmotionResponse> {
        return safeApiCall {
            emoticonThemeApiService.getEmoticonByEmotion(emotion)
        }
    }
}