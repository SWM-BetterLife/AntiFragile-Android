package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.llm.LlmModelUrlResponse
import com.betterlife.antifragile.data.remote.LlmModelApiService

class LLMModelRepository(
    private val llmModelApiService: LlmModelApiService
) : BaseRepository() {

    suspend fun getLlmModelUrl(): BaseResponse<LlmModelUrlResponse> {
        return safeApiCall {
            llmModelApiService.getLlmModelUrl()
        }
    }
}