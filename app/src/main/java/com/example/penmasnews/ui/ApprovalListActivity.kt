package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.*
import com.example.penmasnews.network.ApprovalService
import com.example.penmasnews.network.EventService
import com.example.penmasnews.util.DateUtils

class ApprovalListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewApproval)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = mutableListOf<ApprovalItem>()
        lateinit var adapter: ApprovalListAdapter
        adapter = ApprovalListAdapter(items) { item, action ->
            val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
            val token = authPrefs.getString("token", null)
            val user = authPrefs.getString("username", "unknown") ?: "unknown"
            val updatedEvent = item.event.copy(
                status = action,
                lastUpdate = DateUtils.now(),
                updatedBy = user
            )
            val requestId = item.request.requestId
            if (token != null) {
                Thread {
                    EventService.updateEvent(token, updatedEvent.id, updatedEvent)
                    ApprovalService.updateApproval(token, requestId, action)
                }.start()
            }
            val pos = items.indexOf(item)
            if (pos != -1) {
                items.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }
        recyclerView.adapter = adapter

        Thread {
            val auth = getSharedPreferences("auth", MODE_PRIVATE)
            val token = auth.getString("token", null)
            if (token != null) {
                val approvals = ApprovalService.fetchApprovals(token)
                    .filter { it.status == "pending" }
                val events = EventService.fetchEvents(token)
                val list = approvals.mapNotNull { ap ->
                    val evt = events.find { it.id == ap.eventId }
                    if (evt != null) ApprovalItem(evt, ap) else null
                }
                runOnUiThread {
                    items.addAll(list)
                    adapter.notifyDataSetChanged()
                }
            }
        }.start()
    }
}
