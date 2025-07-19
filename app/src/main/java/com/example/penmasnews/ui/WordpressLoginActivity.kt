package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import android.widget.TextView
import android.widget.ScrollView
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.model.CMSPrefs
import com.example.penmasnews.feature.WordpressAuth
import com.example.penmasnews.util.DebugLogger
import com.example.penmasnews.util.UrlUtils

class WordpressLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordpress_login)

        val editBase = findViewById<TextInputEditText>(R.id.editWpBase)
        val editUser = findViewById<TextInputEditText>(R.id.editWpUser)
        val editPass = findViewById<TextInputEditText>(R.id.editWpAppPass)
        val button = findViewById<Button>(R.id.buttonWpLogin)
        val logView = findViewById<TextView>(R.id.textWpLog)
        val logScroll = findViewById<ScrollView>(R.id.logScrollView)
        val copyButton = findViewById<Button>(R.id.buttonCopyWpLog)
        val viewButton = findViewById<Button>(R.id.buttonViewWpLog)

        CMSPrefs.getWordpressBaseUrl(this)?.let { editBase.setText(it) }
        CMSPrefs.getWordpressUser(this)?.let { editUser.setText(it) }
        CMSPrefs.getWordpressAppPass(this)?.let { editPass.setText(it) }

        fun refreshLog() {
            logView.text = DebugLogger.readLog(this)
            logScroll.post { logScroll.fullScroll(View.FOCUS_DOWN) }
        }

        refreshLog()

        button.setOnClickListener {
            val base = UrlUtils.ensureHttpScheme(editBase.text.toString())
            val user = editUser.text.toString()
            val pass = editPass.text.toString()
            WordpressAuth.verifyAppPassword(this, base, user, pass) { ok ->
                runOnUiThread {
                    if (ok) {
                        CMSPrefs.saveWordpressCredentials(this, base, user, pass)
                        Toast.makeText(this, R.string.message_login_success, Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, R.string.message_login_failed, Toast.LENGTH_LONG).show()
                    }
                    refreshLog()
                }
            }
        }

        copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("log", logView.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.message_log_copied, Toast.LENGTH_SHORT).show()
        }

        viewButton.setOnClickListener {
            val log = logView.text.toString()
            val regex = "https?://\\S+\\.h?html".toRegex()
            val match = regex.find(log)
            if (match != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(match.value))
                startActivity(intent)
            } else {
                Toast.makeText(this, R.string.message_no_html_log, Toast.LENGTH_LONG).show()
            }
        }
    }
}
