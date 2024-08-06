package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.local.DiaryDao
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary

class DiaryRepository(private val diaryDao: DiaryDao) {

    suspend fun insertTextDiary(textDiary: TextDiary): Long {
        val textDiaryCount = diaryDao.countTextDiariesByDate(textDiary.date)
        val questionDiaryCount = diaryDao.countQuestionDiariesByDate(textDiary.date)

        return if (textDiaryCount == 0 && questionDiaryCount == 0) {
            diaryDao.insertTextDiary(textDiary)
        } else {
            -1
        }
    }

    suspend fun insertQuestionDiary(questionDiary: QuestionDiary): Long {
        val textDiaryCount = diaryDao.countTextDiariesByDate(questionDiary.date)
        val questionDiaryCount = diaryDao.countQuestionDiariesByDate(questionDiary.date)

        return if (textDiaryCount == 0 && questionDiaryCount == 0) {
            diaryDao.insertQuestionDiary(questionDiary)
        } else {
            -1
        }
    }

    suspend fun updateTextDiary(id: Int, content: String): Int {
        return diaryDao.updateTextDiary(id, content)
    }

    fun getTextDiaryById(
        id: Int)
    : TextDiary? = diaryDao.getTextDiaryById(id)

    fun getQuestionDiaryById(
        id: Int
    ): QuestionDiary? = diaryDao.getQuestionDiaryById(id)
}
