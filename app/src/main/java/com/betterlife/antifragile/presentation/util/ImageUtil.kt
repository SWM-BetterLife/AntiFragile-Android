package com.betterlife.antifragile.presentation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.betterlife.antifragile.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

object ImageUtil {

    fun ImageView.setImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .into(this)
    }

    fun ImageView.setCircleImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .transform(CircleCrop())
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .into(view)
        } else {
            view.setImageResource(R.drawable.emoticon_blank)
        }
    }

    @JvmStatic
    @BindingAdapter("circleImageUrl")
    fun loadCircleImage(view: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .transform(CircleCrop())
                .into(view)
        } else {
            view.setImageResource(R.drawable.emoticon_blank)
        }
    }
}