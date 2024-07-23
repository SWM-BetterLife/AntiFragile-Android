package com.betterlife.antifragile.presentation.ui.calendar

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.local.DiaryDatabase
import com.betterlife.antifragile.data.repository.CalendarRepository
import com.betterlife.antifragile.data.repository.DiaryRepository
import com.betterlife.antifragile.databinding.FragmentDiaryCalendarBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModel
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModelFactory
import java.util.Calendar

class DiaryCalendarFragment : BaseFragment<FragmentDiaryCalendarBinding>(R.layout.fragment_diary_calendar) {

    private lateinit var diaryCalendarViewModel: DiaryCalendarViewModel
    private lateinit var diaryCalendarAdapter: DiaryCalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadCurrentMonth()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_nav_calendar_to_nav_diary_create)
        }
    }

    private fun setupViewModel() {
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        val diaryRepository = DiaryRepository(diaryDao)
        val calendarRepository = CalendarRepository(diaryRepository)
        val factory = DiaryCalendarViewModelFactory(calendarRepository, diaryRepository)
        diaryCalendarViewModel = ViewModelProvider(this, factory)[DiaryCalendarViewModel::class.java]
    }

    private fun setupRecyclerView() {
        diaryCalendarAdapter = DiaryCalendarAdapter { dateModel ->
            // 날짜 클릭 시 처리
            dateModel.diaryId?.let { diaryId ->
                // diaryId를 사용하여 일기 상세 화면으로 이동하는 로직
                // 예: navigateToDiaryDetail(diaryId)
            }
        }
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = diaryCalendarAdapter
    }

    private fun setupObservers() {
        diaryCalendarViewModel.calendarDates.observe(viewLifecycleOwner) { dates ->
            diaryCalendarAdapter.setDates(dates)
        }

        diaryCalendarViewModel.currentYearMonth.observe(viewLifecycleOwner) { (year, month) ->
            binding.tvMonthYear.text = String.format("%d.%d", year, month)
        }
    }

    private fun setupListeners() {
        binding.btnPreviousMonth.setOnClickListener {
            diaryCalendarViewModel.moveToPreviousMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            diaryCalendarViewModel.moveToNextMonth()
        }
    }

    private fun loadCurrentMonth() {
        val calendar = Calendar.getInstance()
        diaryCalendarViewModel.loadCalendarDates(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }
}