package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.model.CMSPrefs
import com.example.penmasnews.feature.WordpressAuth

class WordpressLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordpress_login)

        val editBase = findViewById<TextInputEditText>(R.id.editWpBase)
        val editUser = findViewById<TextInputEditText>(R.id.editWpUser)
        val editPass = findViewById<TextInputEditText>(R.id.editWpAppPass)
        val button = findViewById<Button>(R.id.buttonWpLogin)

        CMSPrefs.getWordpressBaseUrl(this)?.let { editBase.setText(it) }
        CMSPrefs.getWordpressUser(this)?.let { editUser.setText(it) }
        CMSPrefs.getWordpressAppPass(this)?.let { editPass.setText(it) }

        button.setOnClickListener {
            val base = editBase.text.toString()
            val user = editUser.text.toString()
            val pass = editPass.text.toString()
            WordpressAuth.login(this, base, user, pass) { token ->
                runOnUiThread {
                    if (token != null) {
                        CMSPrefs.saveWordpressCredentials(this, base, user, "")
                        Toast.makeText(this, R.string.message_login_success, Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, R.string.message_login_failed, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
