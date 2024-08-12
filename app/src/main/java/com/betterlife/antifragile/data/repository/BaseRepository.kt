package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.presentation.util.ApiErrorUtil.parseErrorResponse
import retrofit2.Response

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<BaseResponse<T>>
    ): BaseResponse<T> {

        return try {
            val response = apiCall()
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