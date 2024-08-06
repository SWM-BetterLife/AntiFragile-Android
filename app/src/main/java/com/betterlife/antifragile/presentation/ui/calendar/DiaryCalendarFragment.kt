package com.betterlife.antifragile.presentation.ui.calendar

import android.annotation.SuppressLint
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
import com.betterlife.antifragile.data.model.diary.DiaryType
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

        observeTodayDiaryId()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("일기")
            showCustomButton(R.drawable.btn_alarm) {
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
            diaryCalendarViewModel.setSelectedDate(dateModel.date)
            if (dateModel.diaryId == null && dateModel.date == getCurrentDate()) {
                findNavController().navigate(
                    DiaryCalendarFragmentDirections.actionNavCalendarToNavDiaryTypeSelect(dateModel.date)
                )
            } else {
                when (dateModel.diaryType) {
                    DiaryType.TEXT -> {
                        val action = DiaryCalendarFragmentDirections.actionNavCalendarToNavTextDiaryDetail(
                            dateModel.date,
                            dateModel.diaryId!!
                        )
                        findNavController().navigate(action)
                    }
                    DiaryType.QUESTION -> {
                        val action = DiaryCalendarFragmentDirections.actionNavCalendarToNavQuestionDiaryDetail(
                            dateModel.date,
                            dateModel.diaryId!!
                        )
                    }
                    else -> {
                        // 일기 미작성인 경우
                    }
                }
            }
        }
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = diaryCalendarAdapter
    }

    @SuppressLint("DefaultLocale")
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

        diaryCalendarViewModel.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            diaryCalendarAdapter.setSelectedDate(selectedDate)
        }

        diaryCalendarViewModel.todayDiaryId.observe(viewLifecycleOwner) { diaryId ->
            updateDiaryButtons(diaryId)
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

    private fun observeTodayDiaryId() {
        diaryCalendarViewModel.setTodayDate(getCurrentDate())
    }

    private fun updateDiaryButtons(diaryId: Int?) {
        if (diaryId == null) {
            // 오늘 일기 미작성 시
            binding.btnMoveContent.visibility = View.GONE
            binding.btnAddDiary.visibility = View.VISIBLE
            binding.btnAddDiary.setOnClickListener {
                val action = DiaryCalendarFragmentDirections
                    .actionNavCalendarToNavDiaryTypeSelect(getCurrentDate())
                findNavController().navigate(action)
            }
        } else {
            // 오늘 일기 작성 완료 시
            binding.btnAddDiary.visibility = View.GONE
            binding.btnMoveContent.visibility = View.VISIBLE
            binding.btnMoveContent.setOnClickListener {
                val action = DiaryCalendarFragmentDirections
                    .actionNavCalendarToNavRecommendContent(getCurrentDate(), false)
                findNavController().navigate(action)
            }
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}