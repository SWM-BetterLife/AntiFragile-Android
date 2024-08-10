package com.betterlife.antifragile.presentation.customview

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.animation.TranslateAnimation
import com.betterlife.antifragile.databinding.DialogCustomLoadingBinding

class CustomLoadingDialog(
    context: Context,
    private val emoticonResId: Int,
    private val loadingTexts: Array<String>
) : Dialog(context) {

    private lateinit var binding: DialogCustomLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCustomLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable())
        window!!.setDimAmount(0.8f)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        setupLoading()
    }

    private fun setupLoading() {
        binding.ivEmoticon.setImageResource(emoticonResId)

        val translateAnimation = TranslateAnimation(0f, 0f, -15f, 15f)
        translateAnimation.duration = 1000
        translateAnimation.repeatMode = TranslateAnimation.REVERSE
        translateAnimation.repeatCount = TranslateAnimation.INFINITE
        binding.ivEmoticon.startAnimation(translateAnimation)

        startLoadingTextAnimation()
    }

    private fun startLoadingTextAnimation() {
        var index = 0

        val handler = android.os.Handler()
        val runnable = object : Runnable {
            override fun run() {
                binding.tvLoading.text = loadingTexts[index]
                index = (index + 1) % loadingTexts.size
                handler.postDelayed(this, 500)
            }
        }
        handler.post(runnable)
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}