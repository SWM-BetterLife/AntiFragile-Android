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

    fun getTextDiaryById(
        id: Int)
    : TextDiary = diaryDao.getTextDiaryById(id)

    fun getQuestionDiaryById(
        id: Int
    ): QuestionDiary = diaryDao.getQuestionDiaryById(id)

    fun getMonthlyDiaries(
        month: String
    ): List<DiarySummary> = diaryDao.getMonthlyDiaries(month)
}
