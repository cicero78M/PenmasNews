package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.model.ApprovalRequest
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.network.ApprovalService
import com.example.penmasnews.network.EventService
import com.example.penmasnews.util.DateUtils

class ApprovalDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_detail)

        val event = intent.getSerializableExtra("event") as? EditorialEvent
        val request = intent.getSerializableExtra("request") as? ApprovalRequest

        val textDate = findViewById<TextView>(R.id.textDate)
        val textTitle = findViewById<TextView>(R.id.textTitle)
        val textAssignee = findViewById<TextView>(R.id.textAssignee)
        val textContent = findViewById<TextView>(R.id.textContent)
        val buttonApprove = findViewById<Button>(R.id.buttonApprove)
        val buttonReject = findViewById<Button>(R.id.buttonReject)

        textDate.text = event?.date
        textTitle.text = event?.topic
        textAssignee.text = event?.assignee
        textContent.text = event?.content

        buttonApprove.setOnClickListener {
            handleAction(event, request, "approved")
        }

        buttonReject.setOnClickListener {
            handleAction(event, request, "rejected")
        }
    }

    private fun handleAction(event: EditorialEvent?, request: ApprovalRequest?, status: String) {
        if (event == null || request == null) return
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val user = prefs.getString("username", "unknown") ?: "unknown"
        if (token != null) {
            Thread {
                val updatedEvent = event.copy(
                    status = status,
                    lastUpdate = DateUtils.now(),
                    updatedBy = user
                )
                EventService.updateEvent(token, event.id, updatedEvent)
                ApprovalService.updateApproval(token, request.requestId, status)
            }.start()
        }
        intent.putExtra("requestId", request.requestId)
        intent.putExtra("action", status)
        setResult(RESULT_OK, intent)
        finish()
    }
}
