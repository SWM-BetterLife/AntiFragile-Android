package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextDiaryViewModel(
    private val diaryRepository: DiaryRepository,
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) : ViewModel() {

    private val _textDiaryDetail = MutableLiveData<BaseResponse<TextDiaryDetail>>()
    val textDiaryDetail: LiveData<BaseResponse<TextDiaryDetail>> = _textDiaryDetail


    init {
        _textDiaryDetail.value = BaseResponse(Status.INIT, null, null)
    }

    fun getTextDiaryDetail(id: Int, date: String) {
        viewModelScope.launch {
            _textDiaryDetail.value = BaseResponse(Status.LOADING, null, null)
            _textDiaryDetail.value = getTextDiaryDetailData(id, date)
        }
    }

    private suspend fun getTextDiaryDetailData(
        id: Int, date: String
    ): BaseResponse<TextDiaryDetail> = withContext(Dispatchers.IO) {
        return@withContext try {
            val textDiary = diaryRepository.getTextDiaryById(id)
            val diaryAnalysisResponse = diaryAnalysisRepository.getDailyDiaryAnalysis(date)

            if (textDiary == null) {
                BaseResponse(Status.ERROR, "Not Existed diaryId", null)
            } else {
                val combinedData = TextDiaryDetail(
                    id = textDiary.id,
                    date = textDiary.date,
                    content = textDiary.content,
                    emotions = diaryAnalysisResponse.data?.emotions,
                    emoticonInfo = diaryAnalysisResponse.data?.emoticon
                )

                if (diaryAnalysisResponse.status == Status.SUCCESS) {
                    BaseResponse(
                        Status.SUCCESS,
                        null,
                        combinedData
                    )
                } else {
                    BaseResponse(
                        diaryAnalysisResponse.status,
                        diaryAnalysisResponse.errorMessage,
                        combinedData
                    )
                }
            }
        } catch (e: Exception) {
            BaseResponse(
                Status.ERROR,
                e.message ?: "Unknown error occurred",
                null
            )
        }
    }
}