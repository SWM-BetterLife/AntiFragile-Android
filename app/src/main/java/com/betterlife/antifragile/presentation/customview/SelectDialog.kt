package com.betterlife.antifragile.presentation.customview

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.DialogSelectBinding

class SelectDialog(
    context: Context,
    private val title: String,
    private val description: String,
    private val leftButtonText: String,
    private val rightButtonText: String,
    private val leftButtonListener: (() -> Unit)? = null,
    private val rightButtonListener: (() -> Unit)? = null
) : Dialog(context) {

    private lateinit var binding: DialogSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable())
        window?.setDimAmount(0.7f)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        binding.btnLeft.text = leftButtonText
        binding.btnRight.text = rightButtonText

        // 버튼 리스너 설정
        binding.btnLeft.setOnClickListener {
            leftButtonListener?.invoke()
            dismiss()
        }

        binding.btnRight.setOnClickListener {
            rightButtonListener?.invoke()
            dismiss()
        }

        val titleBackground = GradientDrawable()
        titleBackground.setColor(context.getColor(R.color.white))
        titleBackground.cornerRadii = floatArrayOf(20f, 20f, 20f, 20f, 0f, 0f, 0f, 0f)
        binding.tvTitle.background = titleBackground

        val leftButtonBackground = GradientDrawable()
        leftButtonBackground.setColor(context.getColor(android.R.color.white))
        leftButtonBackground.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 20f, 20f)
        binding.btnLeft.background = leftButtonBackground

        val rightButtonBackground = GradientDrawable()
        rightButtonBackground.setColor(context.getColor(R.color.main_color))
        rightButtonBackground.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 20f, 20f, 0f, 0f)
        binding.btnRight.background = rightButtonBackground
    }
}