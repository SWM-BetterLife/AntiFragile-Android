package com.betterlife.antifragile.presentation.ui.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.diary.DiaryType
import com.betterlife.antifragile.databinding.FragmentDiaryCalendarBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModel
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil.getTodayDate
import java.util.Calendar

class DiaryCalendarFragment : BaseFragment<FragmentDiaryCalendarBinding>(
    R.layout.fragment_diary_calendar
) {

    private lateinit var diaryCalendarViewModel: DiaryCalendarViewModel
    private lateinit var diaryCalendarAdapter: DiaryCalendarAdapter
    private val todayDate = getTodayDate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadCurrentMonth()
        observeTodayDiaryId()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("일기")
        }
    }

    private fun setupViewModel() {
        val factory = DiaryCalendarViewModelFactory(requireContext(), Constants.TOKEN)
        diaryCalendarViewModel =
            ViewModelProvider(this, factory)[DiaryCalendarViewModel::class.java]
    }

    private fun setupRecyclerView() {
        diaryCalendarAdapter = DiaryCalendarAdapter { dateModel ->
            diaryCalendarViewModel.setSelectedDate(dateModel.date)
            if (dateModel.diaryId == null) {
                if (dateModel.date == todayDate) {
                    findNavController().navigate(
                        DiaryCalendarFragmentDirections.actionNavCalendarToNavDiaryTypeSelect(dateModel.date)
                    )
                }
            } else {
                when (dateModel.diaryType) {
                    DiaryType.TEXT -> {
                        val action = DiaryCalendarFragmentDirections.actionNavCalendarToNavTextDiaryDetail(
                            dateModel.diaryId,
                            dateModel.date
                        )
                        findNavController().navigate(action)
                    }
                    DiaryType.QUESTION -> {
                        val action = DiaryCalendarFragmentDirections.actionNavCalendarToNavQuestionDiaryDetail(
                            dateModel.diaryId,
                            dateModel.date
                        )
                        findNavController().navigate(action)
                    }
                    else -> {
                        Log.d("DiaryCalendarFragment", "Unknown diary type: ${dateModel.diaryType}")
                    }
                }
            }
        }
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = diaryCalendarAdapter
    }

    @SuppressLint("DefaultLocale")
    private fun setupObservers() {
        setupBaseObserver(
            liveData = diaryCalendarViewModel.calendarResponse,
            onSuccess = { response ->
                diaryCalendarAdapter.setDates(response)
            },
            onError = {
                diaryCalendarAdapter.setDates(it.data!!)
                Log.d("DiaryCalendarFragment", "Error: ${it.errorMessage}")
            }
        )
        diaryCalendarViewModel.currentYearMonth.observe(viewLifecycleOwner) { (year, month) ->
            binding.tvMonthYear.text = String.format("%d.%d", year, month)
        }

        diaryCalendarViewModel.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            diaryCalendarAdapter.setSelectedDate(selectedDate)
        }

        diaryCalendarViewModel.todayDiaryId.observe(viewLifecycleOwner) { diaryId ->
            if (diaryId != null) {
                dismissAddDiaryComment()
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

    private fun observeTodayDiaryId() {
        diaryCalendarViewModel.setTodayDate(todayDate)
    }

    private fun dismissAddDiaryComment() {
        binding.btnAddDiary.visibility = View.GONE
    }
}