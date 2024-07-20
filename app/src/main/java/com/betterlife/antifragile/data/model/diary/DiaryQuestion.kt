package com.betterlife.antifragile.data.model.diary

enum class DiaryQuestion(val text: String) {
    Q1("오늘 어떤 일이 있었니?"),
    Q2("그때의 너의 감정은 어땠니?"),
    Q3("너는 그때 어떤 생각이 들었니?"),
    Q4("너는 그때 어떤 행동을 취했니?"),
    Q5("미래의 나에게 남기고 싶은 말이 있다면 작성해주세요");

    companion object {
        fun fromText(text: String): DiaryQuestion? = entries.find {
            it.text == text
        }
    }
}