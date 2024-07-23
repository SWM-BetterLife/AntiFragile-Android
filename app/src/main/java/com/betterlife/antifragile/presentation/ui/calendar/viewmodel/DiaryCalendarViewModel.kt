package com.betterlife.antifragile.presentation.ui.calendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.data.repository.CalendarRepository
import com.betterlife.antifragile.data.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryCalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _calendarDates = MutableLiveData<List<CalendarDateModel>>()
    val calendarDates: LiveData<List<CalendarDateModel>> = _calendarDates

    private val _currentYearMonth = MutableLiveData<Pair<Int, Int>>()
    val currentYearMonth: LiveData<Pair<Int, Int>> = _currentYearMonth

    fun loadCalendarDates(year: Int, month: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val dates = calendarRepository.getCalendarDates(year, month)
            withContext(Dispatchers.Main) {
                _calendarDates.value = dates
                _currentYearMonth.value = Pair(year, month)
            }
        }
    }

    fun moveToNextMonth() {
        _currentYearMonth.value?.let { (year, month) ->
            val nextMonth = if (month == 12) 1 else month + 1
            val nextYear = if (month == 12) year + 1 else year
            loadCalendarDates(nextYear, nextMonth)
        }
    }

    fun moveToPreviousMonth() {
        _currentYearMonth.value?.let { (year, month) ->
            val previousMonth = if (month == 1) 12 else month - 1
            val previousYear = if (month == 1) year - 1 else year
            loadCalendarDates(previousYear, previousMonth)
        }
    }

    fun getDiaryIdForDate(date: String): Int? {
        return _calendarDates.value?.find { it.date == date }?.diaryId
    }
}