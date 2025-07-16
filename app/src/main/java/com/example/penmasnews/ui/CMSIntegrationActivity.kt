package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EventStorage

class CMSIntegrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cms_integration)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCms)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)
        val events = EventStorage.loadEvents(prefs).filter { it.status == "disetujui" }

        val adapter = EditorialCalendarAdapter(events.toMutableList())
        recyclerView.adapter = adapter
    }
}
