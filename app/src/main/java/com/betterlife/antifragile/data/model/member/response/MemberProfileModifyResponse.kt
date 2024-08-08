package com.betterlife.antifragile.data.model.member.response

data class MemberProfileModifyResponse(
    val nickname: String,
    val age: Int,
    val gender: Gender,
    val job: String,
    val profileImgUrl: String
)

enum class Gender(val toKorean: String) {
    MALE("남성"),
    FEMALE("여성")
}