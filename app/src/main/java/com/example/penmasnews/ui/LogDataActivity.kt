package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ChangeLogDatabase
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.network.LogService

class LogDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_data)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLogs)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val allLogs = mutableListOf<ChangeLogEntry>()
        allLogs.addAll(ChangeLogDatabase.getLogs(this))
        val adapter = LogListAdapter(allLogs)
        recyclerView.adapter = adapter

        val token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null)
        if (token != null) {
            Thread {
                val events = EventStorage.loadEvents(this)
                val remoteLogs = mutableListOf<ChangeLogEntry>()
                for (event in events) {
                    remoteLogs.addAll(LogService.fetchLogs(token, event.id))
                }
                runOnUiThread {
                    allLogs.addAll(remoteLogs)
                    allLogs.sortByDescending { it.timestamp }
                    adapter.notifyDataSetChanged()
                }
            }.start()
        }
    }
}
