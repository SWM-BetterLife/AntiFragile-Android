package com.betterlife.antifragile.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.betterlife.antifragile.NavMainDirections
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.ActivityMainBinding
import com.betterlife.antifragile.presentation.base.BaseActivity
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModel
import com.betterlife.antifragile.presentation.ui.calendar.viewmodel.DiaryCalendarViewModelFactory
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    lateinit var diaryCalendarViewModel: DiaryCalendarViewModel
    lateinit var navController: NavController
    private val initialFragmentIds = mapOf(
        R.id.nav_calendar to R.id.nav_calendar,
        R.id.nav_emotion to R.id.nav_emotion,
        R.id.nav_content to R.id.nav_content,
        R.id.nav_my_page to R.id.nav_my_page
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupBottomNavigation()
        setupAddDiaryButton()
        observeTodayDiaryId()
    }

    override fun getLayoutResourceId() = R.layout.activity_main

    override fun setupToolbar() {
        toolbar.apply {
            reset()
            setMainTitle("일기")
        }
    }

    private fun setupViewModel() {
        val factory = DiaryCalendarViewModelFactory(this)
        diaryCalendarViewModel = factory.create(DiaryCalendarViewModel::class.java)
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val initialFragmentId = initialFragmentIds[item.itemId]
            initialFragmentId?.let {
                navController.popBackStack(it, false)
                navController.navigate(it)
                true
            } ?: false
        }
    }

    private fun setupAddDiaryButton() {
        binding.btnAddDiary.setOnClickListener {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            diaryCalendarViewModel.setTodayDate(today)
        }
    }

    private fun observeTodayDiaryId() {
        diaryCalendarViewModel.todayDiaryId.observe(this, Observer { diaryId ->
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            Log.d("MainActivity", "todayDiaryId: $diaryId")
            if (diaryId != null) {
                binding.bottomNavigation.selectedItemId = R.id.nav_emotion
            } else {
                val action = NavMainDirections.actionToNavDiaryTypeSelect(today)
                binding.bottomNavigation.selectedItemId = R.id.nav_calendar
                navController.navigate(action)
            }
        })
    }

    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.btnAddDiary.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
        binding.btnAddDiary.visibility = View.GONE
    }
}