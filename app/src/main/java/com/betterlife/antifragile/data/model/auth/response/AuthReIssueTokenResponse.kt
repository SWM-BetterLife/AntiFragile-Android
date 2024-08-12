package com.betterlife.antifragile.data.model.auth.response

data class AuthReIssueTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
