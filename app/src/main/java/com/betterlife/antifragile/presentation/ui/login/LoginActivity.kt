package com.betterlife.antifragile.presentation.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.betterlife.antifragile.databinding.ActivityLoginBinding
import com.google.android.gms.common.SignInButton

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setGoogleButtonText(binding.btnGoogleLogin, "Google 로그인")
        binding.btnGoogleLogin.setOnClickListener { startGoogleLogin() }
    }

    private fun setGoogleButtonText(loginButton: SignInButton, buttonText: String) {
        for (i in 0 until loginButton.childCount) {
            (loginButton.getChildAt(i) as? TextView)?.apply {
                text = buttonText
                gravity = Gravity.CENTER
                return
            }
        }
    }

    private fun startGoogleLogin() {
        val url = "https://dev.better-life-api.com/oauth2/authorization/google"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}