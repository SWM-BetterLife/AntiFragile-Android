package com.betterlife.antifragile.data.model.diary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_diary")
data class QuestionDiary(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // TODO: List로 바꾸기
    val emotions: String,
    val event: String,
    val thought: String,
    val action: String,
    val comment: String,
    val date: String,
)