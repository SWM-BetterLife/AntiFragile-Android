package com.betterlife.antifragile.data.model.auth.request

import com.betterlife.antifragile.data.model.enums.Gender
import com.betterlife.antifragile.data.model.enums.LoginType

data class AuthSignUpRequest(
    val email: String,
    val loginType: LoginType,
    val nickname: String,
    val age: Int,
    val gender: Gender,
    val job: String
)
