package com.betterlife.antifragile.presentation.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.betterlife.antifragile.R

class LoginActivity : AppCompatActivity() {
    private lateinit var googleLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleLoginButton = findViewById(R.id.googleLoginButton)
        googleLoginButton.setOnClickListener { startGoogleLogin() }
    }

    private fun startGoogleLogin() {
        val url = "https://dev.better-life-api.com/oauth2/authorization/google"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}