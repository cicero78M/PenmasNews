package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.ui.TrendingTopicAdapter
import java.util.Calendar

class AnalyticsDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_dashboard)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val viewsText = findViewById<TextView>(R.id.textViews)
        val visitorsText = findViewById<TextView>(R.id.textVisitors)
        val bounceText = findViewById<TextView>(R.id.textBounce)
        val trendingList = findViewById<RecyclerView>(R.id.recyclerViewTrending)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        dateEdit.setText(prefs.getString("date", ""))
        notesEdit.setText(prefs.getString("notes", ""))

        dateEdit.setOnClickListener {
            showDatePicker(dateEdit)
        }

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
                .apply()
        }

        // Placeholder angka metrik, seharusnya diambil dari layanan analitik
        val pageViews = 12345
        val uniqueVisitors = 6789
        val bounceRate = 54.3f
        viewsText.text = getString(R.string.label_page_views) + ": $pageViews"
        visitorsText.text = getString(R.string.label_unique_visitors) + ": $uniqueVisitors"
        bounceText.text = getString(R.string.label_bounce_rate) + ": $bounceRate%"

        val trending = listOf(
            "Pencanangan Zona Integritas",
            "Operasi Pengamanan Mudik",
            "Penangkapan Kasus Narkoba"
        )
        trendingList.layoutManager = LinearLayoutManager(this)
        trendingList.adapter = TrendingTopicAdapter(trending)
    }

    private fun showDatePicker(target: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                // Use ISO format so server parses correctly
                val result = String.format("%04d-%02d-%02d", year, month + 1, day)
                target.setText(result)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
