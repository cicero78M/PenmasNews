package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ApprovalStorage
import com.example.penmasnews.ui.ApprovalListAdapter

class ApprovalListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewApproval)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences(ApprovalStorage.PREFS_NAME, MODE_PRIVATE)
        val events = ApprovalStorage.loadEvents(prefs)

        val adapter = ApprovalListAdapter(events) {
            ApprovalStorage.saveEvents(prefs, events)
        }
        recyclerView.adapter = adapter
    }
}
