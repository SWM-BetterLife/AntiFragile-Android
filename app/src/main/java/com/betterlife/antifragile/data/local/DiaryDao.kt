package com.betterlife.antifragile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.betterlife.antifragile.data.model.diary.DiarySummary
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary

@Dao
interface DiaryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTextDiary(textDiary: TextDiary): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertQuestionDiary(questionDiary: QuestionDiary): Long

    @Query("""
        SELECT id, date, 'TEXT' AS diaryType FROM text_diary WHERE date LIKE :month || '%'
        UNION ALL
        SELECT id, date, 'QUESTION' AS diaryType FROM question_diary WHERE date LIKE :month || '%'
        ORDER BY date
    """)
    fun getMonthlyDiaries(month: String): List<DiarySummary>

    @Query("SELECT * FROM text_diary WHERE id = :id")
    fun getTextDiaryById(id: Int): TextDiary

    @Query("SELECT * FROM question_diary WHERE id = :id")
    fun getQuestionDiaryById(id: Int): QuestionDiary

    @Query("SELECT COUNT(*) FROM text_diary WHERE date = :date")
    fun countTextDiariesByDate(date: String): Int

    @Query("SELECT COUNT(*) FROM question_diary WHERE date = :date")
    fun countQuestionDiariesByDate(date: String): Int
}