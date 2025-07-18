package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ChangeLogDatabase

class LogDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_data)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLogs)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val logs = ChangeLogDatabase.getLogs(this)
        recyclerView.adapter = LogListAdapter(logs)
    }
}
