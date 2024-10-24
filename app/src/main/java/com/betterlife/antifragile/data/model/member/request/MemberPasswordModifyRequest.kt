package com.betterlife.antifragile.data.model.member.request

data class MemberPasswordModifyRequest (
    val curPassword: String,
    val newPassword: String
)