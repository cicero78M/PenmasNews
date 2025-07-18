package com.example.penmasnews.ui

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.*
import com.example.penmasnews.network.ApprovalService
import com.example.penmasnews.network.EventService

class ApprovalListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewApproval)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = mutableListOf<ApprovalItem>()
        lateinit var adapter: ApprovalListAdapter
        adapter = ApprovalListAdapter(items) { item ->
            val intent = Intent(this, ApprovalDetailActivity::class.java)
            intent.putExtra("event", item.event)
            intent.putExtra("request", item.request)
            startActivityForResult(intent, 100)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val requestId = data.getIntExtra("requestId", -1)
            val index = items.indexOfFirst { it.request.requestId == requestId }
            if (index != -1) {
                items.removeAt(index)
                findViewById<RecyclerView>(R.id.recyclerViewApproval).adapter?.notifyItemRemoved(index)
            }
        }
    }
}
