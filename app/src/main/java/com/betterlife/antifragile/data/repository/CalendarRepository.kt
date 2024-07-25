package com.betterlife.antifragile.data.repository

import android.annotation.SuppressLint
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.data.model.diary.DiarySummary
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryEmoticon
import java.util.Calendar

class CalendarRepository(private val diaryRepository: DiaryRepository,
                         private val diaryAnalysisRepository: DiaryAnalysisRepository) {

    fun getCalendarDates(year: Int, month: Int): List<CalendarDateModel> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)

        val dates = mutableListOf<CalendarDateModel>()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // 이전 달의 날짜 추가
        for (i in 0 until firstDayOfWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            dates.add(0, CalendarDateModel(formatDate(calendar), false))
        }

        // 현재 달의 날짜 추가
        calendar.set(year, month - 1, 1)
        for (i in 1..daysInMonth) {
            dates.add(CalendarDateModel(formatDate(calendar), true))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 다음 달의 날짜 추가
        while (dates.size < 42) {
            dates.add(CalendarDateModel(formatDate(calendar), false))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 해당 월의 일기 데이터 가져오기
        val diaries = getMonthlyDiaries(year, month)
        val emoticons = getMonthlyEmoticons(year, month)

        // 일기 데이터와 날짜 데이터 매칭
        return dates.map { date ->
            val diary = diaries.find { it.date == date.date }
            val emoticon = emoticons.find { it.date == date.date }
            date.copy(
                emoticonUrl = emoticon?.emoticonUrl,
                diaryId = diary?.id
            )
        }
    }

    @SuppressLint("DefaultLocale")
    fun getMonthlyDiaries(year: Int, month: Int): List<DiarySummary> {
        val monthString = String.format("%04d-%02d", year, month)
        return diaryRepository.getMonthlyDiaries(monthString)
    }

    @SuppressLint("DefaultLocale")
    fun getMonthlyEmoticons(year: Int, month: Int): List<DiaryEmoticon> {
        val monthString = String.format("%04d-%02d", year, month)
        // TODO: 월별 이모티콘 조회 api로 데이터 가져오기
        // return diaryAnalysisApiService.getMonthlyEmoticons(year, month)
        return arrayListOf() // 임시로 빈 리스트 반환
    }

    @SuppressLint("DefaultLocale")
    private fun formatDate(calendar: Calendar): String {
        return String.format(
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}