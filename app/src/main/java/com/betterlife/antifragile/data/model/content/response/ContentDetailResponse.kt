package com.betterlife.antifragile.data.model.content.response

data class ContentDetailResponse(
    val url: String,
    val appStats: AppStats,
    val isLiked: Boolean,
    val isSaved: Boolean
)

data class AppStats(
    val likeNumber: Long,
    val viewNumber: Long,
    val saveNumber: Long
)