package com.betterlife.antifragile.presentation.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.local.DiaryDatabase
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.CalendarRepository
import com.betterlife.antifragile.databinding.FragmentDiaryCalendarBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModel
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import java.util.Calendar

class DiaryCalendarFragment : BaseFragment<FragmentDiaryCalendarBinding>(
    R.layout.fragment_diary_calendar
) {

    private lateinit var diaryCalendarViewModel: DiaryCalendarViewModel
    private lateinit var diaryCalendarAdapter: DiaryCalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadCurrentMonth()

        // TODO: 날짜 클릭 시 해당 일기 상세 화면으로 이동하는 로직 구현

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_nav_calendar_to_nav_diary_create)
        }
    }

    private fun setupViewModel() {
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()

        // TODO: 로그인 구현 후, preference나 다른 방법으로 token을 받아와야 함
        val token = Constants.TOKEN
        val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)

        val calendarRepository = CalendarRepository(diaryDao, diaryAnalysisApiService)
        val factory = DiaryCalendarViewModelFactory(calendarRepository)
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

        diaryCalendarViewModel.apiStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                }
                Status.FAIL -> {
                    dismissLoading()
                    diaryCalendarViewModel.
                    errorMessage.value?.let { message ->
                        showCustomToast(message)
                    }
                }
                Status.ERROR -> {
                    dismissLoading()
                    diaryCalendarViewModel.errorMessage.value?.let { message ->
                        showCustomToast(message)
                    }
                }
                else -> {
                    Log.d("EmotionAnalysisFragment", "Unknown status: ${status}")
                }
            }
        }
    }

    private fun loadCurrentMonth() {
        val calendar = Calendar.getInstance()
        diaryCalendarViewModel.loadCalendarDates(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }
}