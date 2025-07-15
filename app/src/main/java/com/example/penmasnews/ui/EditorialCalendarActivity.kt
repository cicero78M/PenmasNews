package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent

class EditorialCalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editorial_calendar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data to illustrate the calendar design
        val events = listOf(
            EditorialEvent("1 Jan", "Refleksi Awal Tahun", "Andi", "draft"),
            EditorialEvent("5 Jan", "Tren Teknologi 2024", "Budi", "review"),
            EditorialEvent("10 Jan", "Wawancara Tokoh", "Citra", "publish")
        )

        recyclerView.adapter = EditorialCalendarAdapter(events)
    }
}
