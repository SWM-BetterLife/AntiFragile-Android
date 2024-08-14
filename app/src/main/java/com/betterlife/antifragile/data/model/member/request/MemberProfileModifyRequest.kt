package com.betterlife.antifragile.data.model.member.request

import com.betterlife.antifragile.data.model.enums.Gender

data class MemberProfileModifyRequest(
    val nickname: String,
    val birthDate: String,
    val gender: Gender,
    val job: String
)
