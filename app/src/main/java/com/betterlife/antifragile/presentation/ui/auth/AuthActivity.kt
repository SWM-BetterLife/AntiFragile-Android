package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.ActivityAuthBinding
import com.betterlife.antifragile.presentation.base.BaseActivity

class AuthActivity : BaseActivity<ActivityAuthBinding>(
    ActivityAuthBinding::inflate
) {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun getLayoutResourceId() = R.layout.activity_auth

    override fun setupToolbar() {
        toolbar.apply {
            reset()
        }
    }
}