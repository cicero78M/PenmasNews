package com.example.penmasnews.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.MainActivity
import com.example.penmasnews.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editUsername = findViewById<EditText>(R.id.editUsername)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val username = editUsername.text.toString()
            val password = editPassword.text.toString()
            if (username == "@papiqo" && password == "12345") {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("actor", "penulis")
                getSharedPreferences("user", MODE_PRIVATE)
                    .edit().putString("username", username).apply()
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
