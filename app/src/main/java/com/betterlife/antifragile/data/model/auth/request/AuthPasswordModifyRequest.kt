package com.betterlife.antifragile.data.model.auth.request

data class AuthPasswordModifyRequest (
    val curPassword: String,
    val newPassword: String
)