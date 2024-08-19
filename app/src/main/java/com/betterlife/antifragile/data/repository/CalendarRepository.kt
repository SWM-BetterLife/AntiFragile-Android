package com.betterlife.antifragile.data.repository

import android.annotation.SuppressLint
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.data.model.diary.DiaryInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class CalendarRepository(
    private val diaryRepository: DiaryRepository,
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) : BaseRepository() {


    suspend fun getCalendarDates(
        year: Int, month: Int
    ): BaseResponse<List<CalendarDateModel>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, 1)

            val dates = mutableListOf<CalendarDateModel>()
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

            for (i in 0 until firstDayOfWeek) {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                dates.add(0, CalendarDateModel(formatDate(calendar), false))
            }

            calendar.set(year, month - 1, 1)
            for (i in 1..daysInMonth) {
                dates.add(CalendarDateModel(formatDate(calendar), true))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            while (dates.size < 42) {
                dates.add(CalendarDateModel(formatDate(calendar), false))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            val diaries = diaryRepository.getMonthlyDiaries(year, month)
            val emoticonsResponse = diaryAnalysisRepository.getMonthlyEmoticons(year, month)

            val emoticons = if (emoticonsResponse.status == Status.SUCCESS) {
                emoticonsResponse.data?.emoticons ?: emptyList()
            } else {
                emptyList()
            }

            val calendarDates = dates.map { date ->
                val diary = diaries.find { it.date == date.date }
                val emoticon = emoticons.find { it.diaryDate == date.date }
                date.copy(
                    diaryType = diary?.diaryType,
                    emoticonUrl = emoticon?.imgUrl,
                    diaryId = diary?.id
                )
            }

            if (emoticonsResponse.status == Status.FAIL) {
                BaseResponse(Status.FAIL, emoticonsResponse.errorMessage, calendarDates)
            } else if (emoticonsResponse.status == Status.ERROR) {
                BaseResponse(Status.ERROR, emoticonsResponse.errorMessage, calendarDates)
            } else {
                BaseResponse(Status.SUCCESS, null, calendarDates)
            }
        } catch (e: Exception) {
            BaseResponse(Status.ERROR, e.message, null)
        }
    }

    suspend fun getDiaryInfoByDate(date: String): DiaryInfo? {
        return diaryRepository.getDiaryInfoByDate(date)
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