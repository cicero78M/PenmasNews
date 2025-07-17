package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.network.AuthService
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val editUsername = findViewById<TextInputEditText>(R.id.editSignupUsername)
        val editPassword = findViewById<TextInputEditText>(R.id.editSignupPassword)
        val buttonSignup = findViewById<Button>(R.id.buttonSignupSubmit)

        buttonSignup.setOnClickListener {
            val username = editUsername.text.toString()
            val password = editPassword.text.toString()
            Thread {
                val result = AuthService.signup(username, password, "penulis")
                runOnUiThread {
                    if (result.success) {
                        Toast.makeText(this, R.string.signup_success, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = result.raw ?: result.message ?: getString(R.string.error_signup)
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("signup_error", msg))
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

                    }
                }
            }.start()
        }
    }
}
