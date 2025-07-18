package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.example.penmasnews.R
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.feature.CMSIntegration
import com.example.penmasnews.network.EventService
import com.example.penmasnews.util.DateUtils

class CMSIntegrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cms_integration)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCms)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val events = EventStorage.loadEvents(this).filter { it.status == "approved" }

        val cms = CMSIntegration()
        val adapter = CmsIntegrationAdapter(events) { event ->
            Thread {
                val success = cms.publishToBlogspot(event)
                val token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null)
                val user = getSharedPreferences("auth", MODE_PRIVATE).getString("username", "") ?: ""
                if (success && token != null) {
                    val updated = event.copy(
                        status = "published",
                        lastUpdate = DateUtils.now(),
                        updatedBy = user
                    )
                    EventService.updateEvent(token, event.id, updated)
                }
                runOnUiThread {
                    val msg = if (success) "Dipublikasikan" else "Gagal publish"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }.start()
        }
        recyclerView.adapter = adapter
    }
}
