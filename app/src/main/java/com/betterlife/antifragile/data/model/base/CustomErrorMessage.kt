package com.betterlife.antifragile.data.model.base

enum class CustomErrorMessage(val message: String) {
    DIARY_ANALYSIS_NOT_FOUND("해당 날짜에 사용자의 일기 분석을 찾을 수 없습니다"),
    MEMBER_NOT_FOUND("멤버를 찾을 수 없습니다"),
    AUTH_LOGIN_NOT_AUTHENTICATED("자격 증명에 실패하였습니다."),
}