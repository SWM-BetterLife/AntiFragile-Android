package com.betterlife.antifragile.presentation.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

object ImageUtil {

    fun ImageView.loadImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .into(this)
    }

    fun ImageView.loadImageCircle(url: String?) {
        Glide.with(this.context)
            .load(url)
            .transform(CircleCrop())
            .into(this)
    }
}