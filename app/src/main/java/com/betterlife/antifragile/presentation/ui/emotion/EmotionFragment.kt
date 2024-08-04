package com.betterlife.antifragile.presentation.ui.emotion

import android.os.Bundle
import android.view.View
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentEmotionBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class EmotionFragment : BaseFragment<FragmentEmotionBinding>(R.layout.fragment_emotion) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("나의 감정")
            showNotificationButton(true) {
                // TODO: 알림 버튼 클릭 처리
                showCustomToast("알림 버튼 클릭")
            }
            showLine()
        }
    }
}