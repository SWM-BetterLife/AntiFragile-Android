package com.betterlife.antifragile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary

@Dao
interface DiaryDao {
    @Insert
    fun insertTextDiary(textDiary: TextDiary): Long

    @Insert
    fun insertQuestionDiary(questionDiary: QuestionDiary): Long

    @Query("UPDATE text_diary SET emotionIconUrl = :url WHERE id = :id")
    fun updateTextDiaryEmotionIcon(id: Int, url: String): Int

    @Query("UPDATE question_diary SET emotionIconUrl = :url WHERE id = :id")
    fun updateQuestionDiaryEmotionIcon(id: Int, url: String): Int

    @Query("SELECT id, date, emotionIconUrl FROM text_diary WHERE date LIKE :month || '%' UNION ALL SELECT id, date, emotionIconUrl FROM question_diary WHERE date LIKE :month || '%' ORDER BY date")
    fun getMonthlyDiaries(month: String): List<DiarySummary>

    @Query("SELECT * FROM text_diary WHERE id = :id")
    fun getTextDiaryById(id: Int): TextDiary

    @Query("SELECT * FROM question_diary WHERE id = :id")
    fun getQuestionDiaryById(id: Int): QuestionDiary
}

data class DiarySummary(
    val id: Int,
    val date: String,
    val emotionIconUrl: String
)