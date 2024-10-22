package com.betterlife.antifragile.data.model.member.response

import com.betterlife.antifragile.data.model.enums.MemberStatus

data class MemberExistenceResponse(
    val status: MemberStatus
)