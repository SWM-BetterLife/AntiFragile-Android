package com.betterlife.antifragile.presentation.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.betterlife.antifragile.NavMainDirections
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.ActivityMainBinding
import com.betterlife.antifragile.presentation.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(R.id.lo_toolbar)
        setupBottomNavigation()
        setupAddDiaryButton()
    }

    override fun getLayoutResourceId() = R.layout.activity_main

    override fun setupToolbar() {
        toolbar.apply {
            reset()
            setMainTitle("일기")
            showNotificationButton(true) {
                // 알림 버튼 클릭 처리
            }
        }
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null
    }

    private fun isEmotionDiaryWrittenToday(): Boolean {
        // TODO: 오늘 작성한 감정 일기가 있는지 확인하는 로직 구현
        return false
    }

    private fun setupAddDiaryButton() {
        binding.btnAddDiary.setOnClickListener {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (isEmotionDiaryWrittenToday()) {
                val action = NavMainDirections.actionToNavEmotion()
                navController.navigate(action)
            } else {
                val action = NavMainDirections.actionToNavDiaryTypeSelect(today)
                navController.navigate(action)
            }        }
    }
}