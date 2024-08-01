package com.betterlife.antifragile.presentation.ui.main

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.ActivityMainBinding
import com.betterlife.antifragile.presentation.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(R.id.lo_toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null
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
}