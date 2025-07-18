package com.example.penmasnews.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.MainActivity
import com.example.penmasnews.R
import com.example.penmasnews.network.AuthService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editUsername = findViewById<TextInputEditText>(R.id.editUsername)
        val editPassword = findViewById<TextInputEditText>(R.id.editPassword)
        val checkRemember = findViewById<CheckBox>(R.id.checkRememberLogin)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonSignup = findViewById<Button>(R.id.buttonSignup)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        if (prefs.getBoolean("remember_credentials", false)) {
            editUsername.setText(prefs.getString("saved_username", ""))
            editPassword.setText(prefs.getString("saved_password", ""))
            checkRemember.isChecked = true
        }

        buttonLogin.setOnClickListener {
            val username = editUsername.text.toString()
            val password = editPassword.text.toString()
            val editor = prefs.edit()
            if (checkRemember.isChecked) {
                editor.putBoolean("remember_credentials", true)
                editor.putString("saved_username", username)
                editor.putString("saved_password", password)
            } else {
                editor.remove("remember_credentials")
                editor.remove("saved_username")
                editor.remove("saved_password")
            }
            editor.apply()
            Thread {
                val result = AuthService.login(username, password)
                runOnUiThread {
                    if (result.success && result.token != null) {
                        loginUser(
                            username,
                            result.role ?: "penulis",
                            result.token,
                            result.userId ?: ""
                        )
                    } else {
                        Toast.makeText(this, result.message ?: getString(R.string.error_login), Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }

        buttonSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginUser(username: String, role: String, token: String, userId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("actor", role)
        getSharedPreferences("auth", MODE_PRIVATE)
            .edit()
            .putString("username", username)
            .putString("token", token)
            .putString("userId", userId)
            .putString("role", role)
            .apply()
        startActivity(intent)
        finish()
    }
}
