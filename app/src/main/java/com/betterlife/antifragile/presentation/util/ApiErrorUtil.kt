package com.betterlife.antifragile.presentation.util

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ApiErrorUtil {
    fun parseErrorResponse(errorBody: String?): BaseResponse<Any?> {
        return try {
            Gson().fromJson(errorBody, object : TypeToken<BaseResponse<Any?>>() {}.type)
        } catch (e: Exception) {
            BaseResponse(Status.ERROR, "Unknown error occurred", null)
        }
    }}