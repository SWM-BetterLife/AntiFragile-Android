package com.betterlife.antifragile.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.betterlife.antifragile.databinding.LayoutToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutToolbarBinding = LayoutToolbarBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setMainTitle(title: String) {
        binding.tvMainTitle.text = title
        binding.tvMainTitle.visibility = View.VISIBLE
    }

    fun setSubTitle(title: String) {
        binding.tvSubTitle.text = title
        binding.tvSubTitle.visibility = View.VISIBLE
    }

    fun showBackButton(listener: OnClickListener? = null) {
        binding.btnBack.visibility = VISIBLE
        binding.btnBack.setOnClickListener(listener)
    }

    fun showCustomButton(imageResId: Int? = null, listener: View.OnClickListener? = null) {
        binding.btnCustom.visibility = View.VISIBLE
        if (imageResId != null) {
            binding.btnCustom.setImageResource(imageResId)
        }
        binding.btnCustom.setOnClickListener(listener)
    }

    fun showLine() {
        binding.line.visibility = View.VISIBLE
    }

    fun reset() {
        binding.tvMainTitle.visibility = View.INVISIBLE
        binding.tvSubTitle.visibility = View.INVISIBLE
        binding.btnBack.visibility = View.INVISIBLE
        binding.btnCustom.visibility = View.INVISIBLE
        binding.line.visibility = View.INVISIBLE
    }
}