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

    fun showBackButton(show: Boolean, listener: OnClickListener? = null) {
        binding.btnBack.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnBack.setOnClickListener(listener)
    }

    fun showNotificationButton(show: Boolean, listener: OnClickListener? = null) {
        binding.btnNotification.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnNotification.setOnClickListener(listener)
    }

    fun showEditButton(text: String, listener: OnClickListener? = null) {
        binding.btnEdit.apply {
            visibility = View.VISIBLE
            setText(text)
            setOnClickListener(listener)
        }
    }

    fun showLine() {
        binding.line.visibility = View.VISIBLE
    }

    fun reset() {
        binding.tvMainTitle.visibility = View.GONE
        binding.tvSubTitle.visibility = View.GONE
        binding.btnBack.visibility = View.GONE
        binding.btnNotification.visibility = View.GONE
        binding.btnEdit.visibility = View.GONE
        binding.line.visibility = View.GONE
    }
}