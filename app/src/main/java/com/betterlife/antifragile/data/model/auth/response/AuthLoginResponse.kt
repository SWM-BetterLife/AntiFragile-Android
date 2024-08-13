package com.betterlife.antifragile.data.model.auth.response

import com.betterlife.antifragile.data.model.enums.LoginType

data class AuthLoginResponse(
    val id: String,
    val email: String,
    val loginType: LoginType,
    val tokenIssue: TokenIssue
)
