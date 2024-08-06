package com.betterlife.antifragile.data.model.emoticontheme

import com.betterlife.antifragile.data.model.common.Emotion

data class EmotionSelectData(
    val emotion: String,
    val imgUrl: String,
    val emotionEnum: Emotion
)