package com.betterlife.antifragile.data.remote

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.llm.LlmModelUrlResponse
import retrofit2.Response
import retrofit2.http.GET

interface LlmModelApiService {

    @GET("/llm-models")
    suspend fun getLlmModelUrl(): Response<BaseResponse<LlmModelUrlResponse>>
}