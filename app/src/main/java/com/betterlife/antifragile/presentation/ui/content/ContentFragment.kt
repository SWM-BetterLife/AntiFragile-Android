package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.view.View
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentContentBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class ContentFragment : BaseFragment<FragmentContentBinding>(R.layout.fragment_content) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("내 콘텐츠")
            showNotificationButton(true) {
                // TODO: 알림 버튼 클릭 처리
                showCustomToast("알림 버튼 클릭")
            }
        }
    }
}