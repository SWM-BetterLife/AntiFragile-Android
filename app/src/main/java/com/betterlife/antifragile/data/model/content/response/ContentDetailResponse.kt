package com.betterlife.antifragile.data.model.content.response

data class ContentDetailResponse(
    val id: String,
    val title: String,
    val description: String,
    val channel: ChannelResponse,
    val url: String,
    val likeNumber: Long,
    val isLiked: Boolean,
    val isSaved: Boolean
)