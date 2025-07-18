package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.network.LogService

class EventLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_data)

        val header = findViewById<TextView>(R.id.textHeader)
        header.text = getString(R.string.label_change_log)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLogs)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val logs = mutableListOf<ChangeLogEntry>()
        val adapter = LogListAdapter(logs)
        recyclerView.adapter = adapter

        val eventId = intent.getIntExtra("eventId", 0)
        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null)
        if (token != null && eventId != 0) {
            Thread {
                val fetched = LogService.fetchLogs(token, eventId)
                runOnUiThread {
                    logs.addAll(fetched)
                    logs.sortByDescending { it.timestamp }
                    adapter.notifyDataSetChanged()
                }
            }.start()
        }
    }
}
