package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonThemeEmoticonsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/*
 * 감정티콘 테마 API 엔드포인트를 정의하는 인터페이스
 */
interface EmoticonThemeApiService {
    @GET("emoticon-themes/{emoticonThemeId}/emoticons")
    suspend fun getEmoticonThemeEmoticons(
        @Path("emoticonThemeId") emoticonThemeId: String
    ): Response<BaseResponse<EmoticonThemeEmoticonsResponse>>
}