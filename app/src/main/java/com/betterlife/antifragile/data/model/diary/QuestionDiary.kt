package com.betterlife.antifragile.data.model.diary

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "question_diary", indices = [Index(value = ["date"], unique = true)])
data class QuestionDiary(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emotions: List<String>,
    val event: String,
    val thought: String,
    val action: String,
    val comment: String,
    val date: String,
)