package com.betterlife.antifragile.data.model.emoticontheme.response

data class EmoticonThemeEmoticonsResponse(
    val emoticons: List<EmoticonThemeEmoticon>
)

data class EmoticonThemeEmoticon(
    val emotion: String,
    val imgUrl: String
)