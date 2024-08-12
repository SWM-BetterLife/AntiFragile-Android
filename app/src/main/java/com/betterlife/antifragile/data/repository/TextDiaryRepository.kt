package com.betterlife.antifragile.data.repository

import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TextDiaryRepository(
    private val diaryRepository: DiaryRepository,
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) {

    suspend fun getTextDiaryDetailData(
        id: Int, date: String
    ): BaseResponse<TextDiaryDetail> = withContext(Dispatchers.IO) {
        return@withContext try {
            val textDiary = diaryRepository.getTextDiaryById(id)
            val diaryAnalysisResponse = diaryAnalysisRepository.getDailyDiaryAnalysis(date)

            if (textDiary == null) {
                BaseResponse(Status.ERROR, "Not Existed diaryId", null)
            } else {
                val textDiaryDetail = TextDiaryDetail(
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
                        textDiaryDetail
                    )
                } else {
                    BaseResponse(
                        diaryAnalysisResponse.status,
                        diaryAnalysisResponse.errorMessage,
                        textDiaryDetail
                    )
                }
            }
        } catch (e: Exception) {
            BaseResponse(
                Status.ERROR,
                e.message,
                null
            )
        }
    }
}