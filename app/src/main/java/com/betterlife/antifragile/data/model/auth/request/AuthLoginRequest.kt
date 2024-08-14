package com.betterlife.antifragile.data.model.auth.request

import com.betterlife.antifragile.data.model.enums.LoginType

data class AuthLoginRequest(
    val email: String,
    val password: String,
    val loginType: LoginType
)
