package com.example.penmasnews

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.ui.AnalyticsDashboardActivity
import com.example.penmasnews.ui.AssetManagerActivity
import com.example.penmasnews.ui.EditorialCalendarActivity
import com.example.penmasnews.ui.ApprovalListActivity
import com.example.penmasnews.ui.LogDataActivity

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
}
