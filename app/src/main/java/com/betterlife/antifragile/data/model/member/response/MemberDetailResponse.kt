package com.betterlife.antifragile.data.model.member.response

import com.betterlife.antifragile.data.model.enums.LoginType

data class MemberDetailResponse(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImgUrl: String,
    val point: Int,
    val diaryTotalNum: Int,
    val loginType: LoginType
)