package com.betterlife.antifragile.data.model.member.request

data class MemberProfileModifyRequest(
    val nickname: String,
    val age: Int,
    val gender: Gender,
    val job: String
)

enum class Gender(val toKorean: String) {
    MALE("남성"),
    FEMALE("여성")
}