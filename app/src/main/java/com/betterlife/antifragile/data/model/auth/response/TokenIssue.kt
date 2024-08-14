package com.betterlife.antifragile.data.model.auth.response

data class TokenIssue(
    val accessToken: String,
    val refreshToken: String
)