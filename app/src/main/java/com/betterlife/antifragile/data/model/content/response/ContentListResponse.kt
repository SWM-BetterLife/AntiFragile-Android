package com.betterlife.antifragile.data.model.content.response

data class ContentListResponse(
    val contents: List<Content>
)

data class Content(
    val id: String,
    val title: String,
    val channel: ChannelResponse,
    val thumbnailImg: String,
    val likeNumber: Long,
    val isLiked: Boolean
)
