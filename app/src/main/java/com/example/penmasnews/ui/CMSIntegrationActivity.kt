package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.example.penmasnews.R
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.feature.CMSIntegration

class CMSIntegrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cms_integration)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCms)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)
        val events = EventStorage.loadEvents(prefs).filter { it.status == "disetujui" }

        val cms = CMSIntegration()
        val adapter = CmsIntegrationAdapter(events) { event ->
            Thread {
                val success = cms.publishToBlogspot(event)
                runOnUiThread {
                    val msg = if (success) "Dipublikasikan" else "Gagal publish"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }.start()
        }
        recyclerView.adapter = adapter
    }
}
