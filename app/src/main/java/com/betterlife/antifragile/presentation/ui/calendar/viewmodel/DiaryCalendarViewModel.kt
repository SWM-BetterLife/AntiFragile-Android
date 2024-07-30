package com.betterlife.antifragile.presentation.ui.calendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.data.repository.CalendarRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class DiaryCalendarViewModel(
    private val calendarRepository: CalendarRepository,
) : ViewModel() {

    private val _calendarResponse = MutableLiveData<BaseResponse<List<CalendarDateModel>>>()
    val calendarResponse: LiveData<BaseResponse<List<CalendarDateModel>>> = _calendarResponse

    private val _currentYearMonth = MutableLiveData<Pair<Int, Int>>()
    val currentYearMonth: LiveData<Pair<Int, Int>> = _currentYearMonth

    init {
        _calendarResponse.value = BaseResponse(Status.INIT, null, null)
        _currentYearMonth.value = Pair(LocalDate.now().year, LocalDate.now().monthValue)
    }

    fun loadCalendarDates(year: Int, month: Int) {
        viewModelScope.launch {
            _calendarResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = calendarRepository.getCalendarDates(year, month)
            _calendarResponse.postValue(response)
            _calendarResponse.value = response
            _currentYearMonth.value = Pair(year, month)
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
        return _calendarResponse.value?.data?.find { it.date == date }?.diaryId
    }
}