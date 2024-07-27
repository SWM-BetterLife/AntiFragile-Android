package com.betterlife.antifragile.presentation.ui.calendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.data.repository.CalendarRepository
import kotlinx.coroutines.launch

class DiaryCalendarViewModel(
    private val calendarRepository: CalendarRepository,
) : ViewModel() {

    private val _calendarDates = MutableLiveData<List<CalendarDateModel>>()
    val calendarDates: LiveData<List<CalendarDateModel>> = _calendarDates

    private val _currentYearMonth = MutableLiveData<Pair<Int, Int>>()
    val currentYearMonth: LiveData<Pair<Int, Int>> = _currentYearMonth

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status> = _apiStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCalendarDates(year: Int, month: Int) {
        viewModelScope.launch {
            _apiStatus.value = Status.LOADING
            val response = calendarRepository.getCalendarDates(year, month)
            when (response.status) {
                Status.SUCCESS -> {
                    _calendarDates.value = response.data
                    _currentYearMonth.value = Pair(year, month)
                    _apiStatus.value = Status.SUCCESS
                    _errorMessage.value = null
                }
                Status.FAIL -> {
                    _calendarDates.value = response.data ?: emptyList()  // 빈 리스트 처리
                    _currentYearMonth.value = Pair(year, month)
                    _apiStatus.value = Status.FAIL
                    _errorMessage.value = response.errorMessage
                }
                Status.ERROR -> {
                    _apiStatus.value = Status.ERROR
                    _errorMessage.value = response.errorMessage
                }
                else -> {}
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