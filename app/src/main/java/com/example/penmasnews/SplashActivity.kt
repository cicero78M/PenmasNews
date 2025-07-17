package com.example.penmasnews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.network.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val userId = prefs.getString("userId", null)
        if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
            validateToken(token, userId)
        } else {
            navigateToLogin()
        }
    }

    private fun validateToken(token: String, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val ok = AuthService.validateToken(token, userId)
            withContext(Dispatchers.Main) {
                if (ok) {
                    navigateToMain()
                } else {
                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
