package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.penmasnews.model.EventStorage
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent
import java.util.Calendar

class EditorialCalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editorial_calendar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val topicEdit = findViewById<EditText>(R.id.editTopic)
        val assigneeEdit = findViewById<EditText>(R.id.editAssignee)
        val statusEdit = findViewById<EditText>(R.id.editStatus)
        val addButton = findViewById<Button>(R.id.buttonAddEvent)
        val clearButton = findViewById<Button>(R.id.buttonClearAll)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)

        val events = EventStorage.loadEvents(prefs).ifEmpty {
            mutableListOf(
                EditorialEvent("1 Jan", "Refleksi Awal Tahun", "Andi", "draft"),
                EditorialEvent("5 Jan", "Tren Teknologi 2024", "Budi", "review"),
                EditorialEvent("10 Jan", "Wawancara Tokoh", "Citra", "publish")
            )
        }

        // load AI-assisted drafts stored from AIHelperActivity
        // events already include entries saved from AIHelperActivity

        val adapter = EditorialCalendarAdapter(events,
            onOpen = { event ->
                val intent = android.content.Intent(this, CollaborativeEditorActivity::class.java)
                intent.putExtra("title", event.topic)
                intent.putExtra("content", event.content)
                intent.putExtra("assignee", event.assignee)
                intent.putExtra("status", event.status)
                startActivity(intent)
            },
            onDelete = { index ->
                events.removeAt(index)
                EventStorage.saveEvents(prefs, events)
            }
        )
        recyclerView.adapter = adapter

        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        addButton.setOnClickListener {
            val eventsList = EventStorage.loadEvents(prefs)
            val event = EditorialEvent(
                dateEdit.text.toString(),
                topicEdit.text.toString(),
                assigneeEdit.text.toString(),
                statusEdit.text.toString()
            )
            eventsList.add(event)
            EventStorage.saveEvents(prefs, eventsList)
            adapter.addItem(event)
            dateEdit.text.clear()
            topicEdit.text.clear()
            assigneeEdit.text.clear()
            statusEdit.text.clear()
        }

        clearButton.setOnClickListener {
            events.clear()
            EventStorage.saveEvents(prefs, events)
            adapter.notifyDataSetChanged()
        }

        saveButton.setOnClickListener {
            EventStorage.saveEvents(prefs, events)
        }
    }

    private fun showDatePicker(target: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val result = String.format("%02d-%02d-%04d", day, month + 1, year)
                target.setText(result)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // persistence handled by EventStorage
}
