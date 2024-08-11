package com.betterlife.antifragile.data.model.auth

import com.betterlife.antifragile.data.model.enums.LoginType

data class AuthSignUpRequest(
    val email: String,
    val password: String,
    val loginType: LoginType
)
