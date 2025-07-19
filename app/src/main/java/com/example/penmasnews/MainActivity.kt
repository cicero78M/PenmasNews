package com.example.penmasnews

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.penmasnews.ui.AnalyticsDashboardActivity
import com.example.penmasnews.ui.AssetManagerActivity
import com.example.penmasnews.ui.EditorialCalendarActivity
import com.example.penmasnews.ui.ApprovalListActivity
import com.example.penmasnews.ui.LogDataActivity
import com.example.penmasnews.ui.WordpressLoginActivity
import com.example.penmasnews.feature.BloggerAuth
import com.example.penmasnews.model.CMSPrefs

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.getStringExtra("actor")?.let { role ->
            val helloText = findViewById<TextView>(R.id.helloText)
            helloText.text = getString(R.string.hello_actor, role)
        }

        findViewById<Button>(R.id.buttonEditorialCalendar).setOnClickListener {
            startActivity(Intent(this, EditorialCalendarActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAssetManager).setOnClickListener {
            startActivity(Intent(this, AssetManagerActivity::class.java))
        }

        findViewById<Button>(R.id.buttonWorkflow).setOnClickListener {
            startActivity(Intent(this, ApprovalListActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAnalytics).setOnClickListener {
            startActivity(Intent(this, AnalyticsDashboardActivity::class.java))
        }

        findViewById<Button>(R.id.buttonLogData).setOnClickListener {
            startActivity(Intent(this, LogDataActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_login_blogger -> {
                BloggerAuth.startLogin(this) { }
                true
            }
            R.id.menu_login_wordpress -> {
                startActivity(Intent(this, WordpressLoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BloggerAuth.RC_SIGN_IN) {
            BloggerAuth.handleAuthResponse(this, data) { token ->
                if (token != null) {
                    CMSPrefs.saveBloggerToken(this, token)
                    Toast.makeText(this, R.string.message_login_success, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, R.string.message_login_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
