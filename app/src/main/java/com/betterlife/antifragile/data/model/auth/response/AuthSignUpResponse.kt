package com.betterlife.antifragile.data.model.auth.response

import com.betterlife.antifragile.data.model.enums.LoginType

data class AuthSignUpResponse(
    val id: String,
    val email: String,
    val nickname: String,
    val loginType: LoginType,
    val tokenIssue: TokenIssue
)
