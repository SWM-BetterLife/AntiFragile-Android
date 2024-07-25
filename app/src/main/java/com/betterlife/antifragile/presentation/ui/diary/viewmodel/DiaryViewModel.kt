package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.local.DiaryDatabase
import com.betterlife.antifragile.data.model.diary.DiarySummary
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.data.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DiaryRepository
    init {
        val diaryDao = DiaryDatabase.getDatabase(application).diaryDao()
        repository = DiaryRepository(diaryDao)
    }

    private val _monthlyDiaries = MutableLiveData<List<DiarySummary>>()
    val monthlyDiaries: LiveData<List<DiarySummary>> = _monthlyDiaries

    fun loadMonthlyDiaries(month: String) = viewModelScope.launch {
        val diaries = withContext(Dispatchers.IO) {
            repository.getMonthlyDiaries(month)
        }
        _monthlyDiaries.value = diaries
    }

    fun insertTextDiary(textDiary: TextDiary): LiveData<Long> = liveData {
        val diaryId = withContext(Dispatchers.IO) {
            repository.insertTextDiary(textDiary)
        }
        emit(diaryId)
    }

    fun insertQuestionDiary(questionDiary: QuestionDiary): LiveData<Long> = liveData {
        val diaryId = withContext(Dispatchers.IO) {
            repository.insertQuestionDiary(questionDiary)
        }
        emit(diaryId)
    }

    fun getTextDiaryById(id: Int): LiveData<TextDiary> = liveData {
        val diary = withContext(Dispatchers.IO) {
            repository.getTextDiaryById(id)
        }
        emit(diary)
    }

    fun getQuestionDiaryById(id: Int): LiveData<QuestionDiary> = liveData {
        val diary = withContext(Dispatchers.IO) {
            repository.getQuestionDiaryById(id)
        }
        emit(diary)
    }
}
