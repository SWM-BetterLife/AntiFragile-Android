package com.betterlife.antifragile.data.model.diary

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "text_diary")
data class TextDiary(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val date: String,
)