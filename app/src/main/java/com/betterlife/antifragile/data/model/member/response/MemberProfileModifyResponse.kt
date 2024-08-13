package com.betterlife.antifragile.data.model.member.response

import com.betterlife.antifragile.data.model.enums.Gender

data class MemberProfileModifyResponse(
    val nickname: String,
    val birthDate: String,
    val gender: Gender,
    val job: String,
    val profileImgUrl: String
)