package com.betterlife.antifragile.data.model.diary

import android.os.Parcelable
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryEmoticon
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextDiaryDetail(
    val id: Int,
    val date: String,
    val content: String,
    val emotions: List<String>?,
    val emoticonInfo: DiaryEmoticon?
) : Parcelable