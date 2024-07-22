package com.betterlife.antifragile.presentation.ui.calendar

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentDiaryCalendarBinding
import com.betterlife.antifragile.presentation.base.BaseFragment

class DiaryCalendarFragment : BaseFragment<FragmentDiaryCalendarBinding>(R.layout.fragment_diary_calendar) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_nav_calendar_to_nav_diary_create)
        }
    }
}