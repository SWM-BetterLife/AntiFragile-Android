package com.betterlife.antifragile.presentation.ui.mypage

import android.os.Bundle
import android.view.View
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentMyPageBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("마이페이지")
            showCustomButton {
                // TODO: 알림 버튼 클릭 처리
                showCustomToast("알림 버튼 클릭")
            }
            showLine()
        }
    }
}