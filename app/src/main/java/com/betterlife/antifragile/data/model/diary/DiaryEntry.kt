package com.betterlife.antifragile.data.model.diary

import androidx.room.*
import java.time.LocalDate

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey
    val id: String,

    val date: LocalDate,

    val type: DiaryType,

    val content: String?,

    val questions: List<QuestionAnswer>?,

    val emoticonUrl: String?
)

data class QuestionAnswer(
    val question: DiaryQuestion,
    val answer: String
)