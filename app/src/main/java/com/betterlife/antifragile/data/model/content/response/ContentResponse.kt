package com.betterlife.antifragile.data.model.content.response

data class ContentResponse(
    val status: String,
    val errorMessage: String?,
    val data: ContentData
)

data class ContentData(
    val contents: List<Content>
)

data class Content(
    val id: String,
    val title: String,
    val description: String,
    val channel: Channel,
    val thumbnailImg: String,
    val videoStats: VideoStats
)

data class Channel(
    val name: String,
    val img: String,
    val subscribeNumber: Long
)

data class VideoStats(
    val viewNumber: Long,
    val likeNumber: Long
)
