package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import org.json.JSONArray
import org.json.JSONObject
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
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val addButton = findViewById<Button>(R.id.buttonAddEvent)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        dateEdit.setText(prefs.getString("date", ""))
        topicEdit.setText(prefs.getString("topic", ""))
        notesEdit.setText(prefs.getString("notes", ""))

        val events = loadEvents(prefs).ifEmpty {
            mutableListOf(
                EditorialEvent("1 Jan", "Refleksi Awal Tahun", "Andi", "draft"),
                EditorialEvent("5 Jan", "Tren Teknologi 2024", "Budi", "review"),
                EditorialEvent("10 Jan", "Wawancara Tokoh", "Citra", "publish")
            )
        }

        val adapter = EditorialCalendarAdapter(events)
        recyclerView.adapter = adapter

        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        addButton.setOnClickListener {
            val eventsList = loadEvents(prefs)
            val event = EditorialEvent(
                dateEdit.text.toString(),
                topicEdit.text.toString(),
                "",
                notesEdit.text.toString()
            )
            eventsList.add(event)
            saveEvents(prefs, eventsList)
            adapter.addItem(event)
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("topic", topicEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
                .apply()
        }

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("topic", topicEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
                .apply()
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

    private fun loadEvents(prefs: SharedPreferences): MutableList<EditorialEvent> {
        val json = prefs.getString("events", "[]") ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<EditorialEvent>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                EditorialEvent(
                    obj.optString("date"),
                    obj.optString("topic"),
                    obj.optString("assignee"),
                    obj.optString("status")
                )
            )
        }
        return list
    }

    private fun saveEvents(prefs: SharedPreferences, events: List<EditorialEvent>) {
        val array = JSONArray()
        for (item in events) {
            val obj = JSONObject()
            obj.put("date", item.date)
            obj.put("topic", item.topic)
            obj.put("assignee", item.assignee)
            obj.put("status", item.status)
            array.put(obj)
        }
        prefs.edit().putString("events", array.toString()).apply()
    }
}
