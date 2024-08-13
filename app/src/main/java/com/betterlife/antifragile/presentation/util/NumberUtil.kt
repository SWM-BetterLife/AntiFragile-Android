package com.betterlife.antifragile.presentation.util

import android.annotation.SuppressLint

object NumberUtil {
    @SuppressLint("DefaultLocale")
    fun formatSubscriberCountKoreanStyle(count: Long): String {
        return when {
            count >= 1_000_000_000 -> String.format("%d억", count / 1_000_000_000)
            count >= 10_000_000 -> String.format("%d천만", count / 10_000_000)
            count >= 1_000_000 -> String.format("%d백만", count / 1_000_000)
            count >= 100_000 -> String.format("%d십만", count / 100_000)
            count >= 10_000 -> String.format("%d만", count / 10_000)
            count >= 1_000 -> String.format("%d천", count / 1_000)
            else -> count.toString()
        }
    }
}