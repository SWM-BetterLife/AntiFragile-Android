package com.betterlife.antifragile.data.model.base

enum class CustomErrorMessage(val message: String) {
    DIARY_ANALYSIS_NOT_FOUND("해당 날짜에 사용자의 일기 분석을 찾을 수 없습니다"),
    DIARY_ANALYSIS_ALREADY_EXIST("이미 해당 날짜에 사용자의 일기 분석이 존재합니다"),
    MEMBER_NOT_FOUND("멤버를 찾을 수 없습니다"),
}