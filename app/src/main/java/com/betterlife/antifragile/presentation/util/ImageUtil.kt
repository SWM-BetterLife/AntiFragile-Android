package com.betterlife.antifragile.presentation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.betterlife.antifragile.R
import com.bumptech.glide.Glide

object ImageUtil {

    fun ImageView.setImage(url: String?) {
        Glide.with(this.context)
            .load(url)
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
}