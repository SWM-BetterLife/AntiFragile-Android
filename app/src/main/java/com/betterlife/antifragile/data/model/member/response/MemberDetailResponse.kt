package com.betterlife.antifragile.data.model.member.response

import com.betterlife.antifragile.data.model.enums.Gender

data class MemberDetailResponse(
    val nickname: String,
    val profileImgUrl: String?,
    val birthDate: String,
    val job: String,
    val gender: Gender
)
