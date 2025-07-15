package com.example.penmasnews

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.ui.AIHelperActivity
import com.example.penmasnews.ui.AnalyticsDashboardActivity
import com.example.penmasnews.ui.AssetManagerActivity
import com.example.penmasnews.ui.CMSIntegrationActivity
import com.example.penmasnews.ui.CollaborativeEditorActivity
import com.example.penmasnews.ui.EditorialCalendarActivity
import com.example.penmasnews.ui.WorkflowManagerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonEditorialCalendar).setOnClickListener {
            startActivity(Intent(this, EditorialCalendarActivity::class.java))
        }

        findViewById<Button>(R.id.buttonCollaborative).setOnClickListener {
            startActivity(Intent(this, CollaborativeEditorActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAIHelper).setOnClickListener {
            startActivity(Intent(this, AIHelperActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAssetManager).setOnClickListener {
            startActivity(Intent(this, AssetManagerActivity::class.java))
        }

        findViewById<Button>(R.id.buttonWorkflow).setOnClickListener {
            startActivity(Intent(this, WorkflowManagerActivity::class.java))
        }

        findViewById<Button>(R.id.buttonCMS).setOnClickListener {
            startActivity(Intent(this, CMSIntegrationActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAnalytics).setOnClickListener {
            startActivity(Intent(this, AnalyticsDashboardActivity::class.java))
        }
    }
}
