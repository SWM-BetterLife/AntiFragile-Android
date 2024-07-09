package com.betterlife.antifragile.presentation.customview

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.betterlife.antifragile.databinding.DialogLoadingBinding

class LoadingDialog(context : Context) : Dialog(context) {

    private lateinit var binding: DialogLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이얼로그가 바깥 터치로 취소되지 않도록 설정
        setCanceledOnTouchOutside(false)
        // 다이얼로그가 취소되지 않도록 설정
        setCancelable(false)
        // 다이얼로그 배경을 투명하게 설정
        window!!.setBackgroundDrawable(ColorDrawable())
        // 배경 어둡게 설정 (0.5f: 어두운 정도)
        window!!.setDimAmount(0.5f)
    }

    // 다이얼로그를 보여주는 메서드
    override fun show() {
        if(!this.isShowing) super.show()
    }
}