package com.betterlife.antifragile.data.model.emoticontheme.response

data class EmoticonsByEmotionResponse(
    val emoticons: List<EmoticonByEmotion>
)

data class EmoticonByEmotion(
    val emoticonThemeId: String,
    val imgUrl: String
)
