package com.betterlife.antifragile.presentation.di

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Customize Glide options here
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}