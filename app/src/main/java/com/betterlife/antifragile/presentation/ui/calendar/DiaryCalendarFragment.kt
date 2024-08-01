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
import com.betterlife.antifragile.presentation.util.CustomToolbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
            val todayDate = getCurrentDate()
            val action = DiaryCalendarFragmentDirections.actionNavCalendarToNavDiaryCreate(todayDate)
            findNavController().navigate(action)
        }
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("일기")
            showNotificationButton(true) {
                // TODO:  알림 버튼 클릭 처리
                showCustomToast("알림 버튼 클릭")
            }
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
        diaryCalendarViewModel.calendarResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }

                Status.SUCCESS -> {
                    dismissLoading()
                    response.data?.let { dates ->
                        diaryCalendarAdapter.setDates(dates)
                    }
                }

                Status.FAIL, Status.ERROR -> {
                    dismissLoading()
                    response.data?.let { dates ->
                        diaryCalendarAdapter.setDates(dates)
                    }
                    showCustomToast(response.errorMessage ?: "일기 데이터 로딩에 실패했습니다.")
                }

                else -> {
                    Log.d("DiaryCalendarFragment", "Unknown status: ${response.status}")
                }
            }
            diaryCalendarViewModel.currentYearMonth.observe(viewLifecycleOwner) { (year, month) ->
                binding.tvMonthYear.text = String.format("%d.%d", year, month)
            }
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

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}