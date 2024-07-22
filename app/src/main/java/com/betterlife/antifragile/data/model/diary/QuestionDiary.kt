package com.betterlife.antifragile.data.model.diary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_diary")
data class QuestionDiary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    val answer5: String,
    val date: String,
    val emotionIconUrl: String?
)