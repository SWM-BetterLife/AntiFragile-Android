package com.betterlife.antifragile.data.model.member.response

import com.betterlife.antifragile.data.model.enums.Gender

data class MemberProfileModifyResponse(
    val nickname: String,
    val age: Int,
    val gender: Gender,
    val job: String,
    val profileImgUrl: String
)