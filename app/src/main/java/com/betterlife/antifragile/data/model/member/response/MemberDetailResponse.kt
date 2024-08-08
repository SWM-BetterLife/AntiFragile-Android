package com.betterlife.antifragile.data.model.member.response

data class MemberDetailResponse(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImgUrl: String,
    val point: Int,
    val diaryTotalNum: Int,
    val loginType: LoginType
)

enum class LoginType(val toKorean: String) {
    GOOGLE("구글"),
    NAVER("네이버"),
    KAKAO("카카오")
}