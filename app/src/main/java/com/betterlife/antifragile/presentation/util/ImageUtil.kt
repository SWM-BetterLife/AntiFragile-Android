package com.betterlife.antifragile.presentation.util

import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageUtil {

    fun ImageView.loadImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .into(this)
    }
}