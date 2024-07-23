package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.local.DiaryDao
import com.betterlife.antifragile.data.model.diary.DiarySummary
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary

class DiaryRepository(private val diaryDao: DiaryDao) {
    fun insertTextDiary(
        textDiary: TextDiary
    ): Long = diaryDao.insertTextDiary(textDiary)

    fun insertQuestionDiary(
        questionDiary: QuestionDiary
    ): Long = diaryDao.insertQuestionDiary(questionDiary)

    fun updateTextDiaryEmotionIcon(
        id: Int,
        url: String
    ) = diaryDao.updateTextDiaryEmotionIcon(id, url)

    fun updateQuestionDiaryEmotionIcon(
        id: Int,
        url: String
    ) = diaryDao.updateQuestionDiaryEmotionIcon(id, url)

    fun getTextDiaryById(
        id: Int)
    : TextDiary = diaryDao.getTextDiaryById(id)

    fun getQuestionDiaryById(
        id: Int
    ): QuestionDiary = diaryDao.getQuestionDiaryById(id)

    fun getMonthlyDiaries(month: String): List<DiarySummary> {
        return diaryDao.getMonthlyDiaries(month)
    }
}
