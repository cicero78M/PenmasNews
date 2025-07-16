package com.example.penmasnews.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.MainActivity
import com.example.penmasnews.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editUsername = findViewById<TextInputEditText>(R.id.editUsername)
        val editPassword = findViewById<TextInputEditText>(R.id.editPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val username = editUsername.text.toString()
            val password = editPassword.text.toString()

            when {
                username == "@papiqo" && password == "12345" -> {
                    loginUser(username, "penulis")
                }
                username == "@penmas" && password == "12345" -> {
                    loginUser(username, "editor")
                }
                else -> {
                    Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loginUser(username: String, role: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("actor", role)
        getSharedPreferences("user", MODE_PRIVATE)
            .edit().putString("username", username).apply()
        startActivity(intent)
        finish()
    }
}
