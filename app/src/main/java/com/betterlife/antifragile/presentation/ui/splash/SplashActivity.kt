package com.betterlife.antifragile.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.betterlife.antifragile.R
import com.betterlife.antifragile.presentation.ui.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            // TODO: 로그인 상태 여부에 따라 login 화면 또는 main 화면으로 이동
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish()
        }, 2000) // 시간 2초 이후 실행
    }
}