package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.model.ChangeLogStorage
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent
import java.util.Calendar
import java.time.LocalDateTime

class EditorialCalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editorial_calendar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val topicEdit = findViewById<EditText>(R.id.editTopic)
        val assigneeEdit = findViewById<EditText>(R.id.editAssignee)
        val statusEdit = findViewById<AutoCompleteTextView>(R.id.editStatus)
        val addButton = findViewById<Button>(R.id.buttonAddEvent)

        val statusList = resources.getStringArray(R.array.status_array)
        statusEdit.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        )

        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)

        // load events on a background thread to avoid NetworkOnMainThreadException
        val events = mutableListOf<EditorialEvent>()

        val adapter = EditorialCalendarAdapter(
            events,
            onOpen = { _, index ->
                val intent = android.content.Intent(this, CollaborativeEditorActivity::class.java)
                intent.putExtra("index", index)
                startActivity(intent)
            },
            onAiAssist = { event, index ->
                val intent = android.content.Intent(this, AIHelperActivity::class.java)
                intent.putExtra("index", index)
                intent.putExtra("date", event.date)
                intent.putExtra("title", event.topic)
                intent.putExtra("assignee", event.assignee)
                startActivity(intent)
            },
            onDelete = { item, _ ->
                if (item.id != 0) {
                    Thread { EventStorage.deleteEvent(this, item.id) }.start()
                }
            }
        )
        recyclerView.adapter = adapter

        // Fetch events asynchronously
        Thread {
            val loaded = EventStorage.loadEvents(this)
            runOnUiThread {
                events.addAll(loaded)
                adapter.notifyDataSetChanged()
            }
        }.start()

        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        addButton.setOnClickListener {
            val assignee = assigneeEdit.text.toString()
            val status = statusEdit.text.toString()
            if (status !in statusList) {
                Snackbar.make(addButton, R.string.error_invalid_status, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userPrefs = getSharedPreferences("user", MODE_PRIVATE)
            val creator = userPrefs.getString("username", "") ?: ""
            val event = EditorialEvent(
                dateEdit.text.toString(),
                topicEdit.text.toString(),
                assignee,
                status,
                "",
                "",
                "",
                0,
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
                creator
            )
            Thread {
                val created = EventStorage.addEvent(this, event)
                runOnUiThread {
                    if (created != null) {
                        events.add(created)
                        adapter.addItem(created)
                    }
                    // log creation of new calendar event
                    val logPrefs = getSharedPreferences(ChangeLogStorage.PREFS_NAME, MODE_PRIVATE)
                    val logs = ChangeLogStorage.loadLogs(logPrefs)
                    val user = userPrefs.getString("username", "unknown") ?: "unknown"
                    val changesDesc = listOf("date", "topic", "assignee", "status").joinToString(", ")
                    logs.add(
                        ChangeLogEntry(
                            user,
                            event.status,
                            changesDesc,
                            System.currentTimeMillis() / 1000L
                        )
                    )
                    ChangeLogStorage.saveLogs(logPrefs, logs)
                    dateEdit.text.clear()
                    topicEdit.text.clear()
                    assigneeEdit.text.clear()
                    statusEdit.text.clear()
                }
            }.start()
        }

    }

    private fun showDatePicker(target: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                // Format using YYYY-MM-DD to match backend expectations
                val result = String.format("%04d-%02d-%02d", year, month + 1, day)
                target.setText(result)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // persistence handled by EventStorage
}
