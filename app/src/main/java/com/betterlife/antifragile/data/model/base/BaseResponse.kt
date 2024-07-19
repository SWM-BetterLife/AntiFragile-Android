package com.betterlife.antifragile.data.model.base

data class BaseResponse<T> (
    val status: Status,
    val errorMessage: String?,
    val data: T?
)

enum class Status {
    SUCCESS, FAIL, ERROR
}
