package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.network.LogService
import com.example.penmasnews.feature.CMSIntegration
import com.example.penmasnews.feature.BloggerAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.penmasnews.network.EventService
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent
import java.util.Calendar
import com.example.penmasnews.util.DateUtils

class EditorialCalendarActivity : AppCompatActivity() {

    private val events = mutableListOf<EditorialEvent>()
    private lateinit var adapter: EditorialCalendarAdapter
    private var pendingPublish: EditorialEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editorial_calendar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val topicEdit = findViewById<EditText>(R.id.editTopic)
        val assigneeEdit = findViewById<EditText>(R.id.editAssignee)
        val addButton = findViewById<Button>(R.id.buttonAddEvent)


        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)

        // adapter backed by [events]
        adapter = EditorialCalendarAdapter(
            events,
            onOpen = { item, _ ->
                val intent = android.content.Intent(this, CollaborativeEditorActivity::class.java)
                intent.putExtra("event", item)
                startActivity(intent)
            },
            onViewLogs = { event, _ ->
                val intent = android.content.Intent(this, EventLogActivity::class.java)
                intent.putExtra("eventId", event.id)
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
            },
            onPublish = { event, _ ->
                val account = BloggerAuth.getSignedInAccount(this)
                if (account == null) {
                    pendingPublish = event
                    BloggerAuth.signIn(this)
                } else {
                    publishEvent(event, account)
                }
            }
        )
        recyclerView.adapter = adapter

        // Fetch events asynchronously
        Thread {
            val loaded = EventStorage.loadEvents(this)
            runOnUiThread {
                events.clear()
                events.addAll(loaded)
                adapter.notifyDataSetChanged()
            }
        }.start()

        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        addButton.setOnClickListener {
            val assignee = assigneeEdit.text.toString()
            val status = "ide"

            val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
            val creator = authPrefs.getString("username", "") ?: ""
            val event = EditorialEvent(
                dateEdit.text.toString(),
                topicEdit.text.toString(),
                assignee,
                status,
                "",
                "",
                "",
                0,
                DateUtils.now(),
                DateUtils.now(),
                creator
            )
            Thread {
                val created = EventStorage.addEvent(this, event)
                val loaded = EventStorage.loadEvents(this)
                runOnUiThread {
                    events.clear()
                    events.addAll(loaded)
                    adapter.notifyDataSetChanged()
                    // log creation of new calendar event to backend
                    val token = authPrefs.getString("token", null)
                    val username = authPrefs.getString("username", "unknown") ?: "unknown"
                    val changesDesc = listOf("date", "topic", "assignee", "status").joinToString(", ")
                    val createdId = created?.id ?: 0
                    if (token != null && createdId != 0) {
                        Thread {
                            LogService.addLog(
                                token,
                                createdId,
                                ChangeLogEntry(username, event.status, changesDesc, System.currentTimeMillis() / 1000L)
                            )
                        }.start()
                    }
                    dateEdit.text.clear()
                    topicEdit.text.clear()
                    assigneeEdit.text.clear()
                }
            }.start()
        }

    }

    override fun onResume() {
        super.onResume()
        // Refresh events whenever returning to this screen
        Thread {
            val loaded = EventStorage.loadEvents(this)
            runOnUiThread {
                events.clear()
                events.addAll(loaded)
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun showDatePicker(target: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                // Backend now returns dd/MM/yyyy so keep the same for input
                val result = String.format("%02d/%02d/%04d", day, month + 1, year)
                target.setText(result)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun publishEvent(event: EditorialEvent, account: com.google.android.gms.auth.api.signin.GoogleSignInAccount) {
        val cms = CMSIntegration()
        Thread {
            val accessToken = BloggerAuth.getAuthToken(this, account)
            val success = cms.publishToBlogspot(event, accessToken)
            val prefsAuth = getSharedPreferences("auth", MODE_PRIVATE)
            val token = prefsAuth.getString("token", null)
            val user = prefsAuth.getString("username", "") ?: ""
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BloggerAuth.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                pendingPublish?.let { publishEvent(it, account!!) }
                pendingPublish = null
            } else {
                Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // persistence handled by EventStorage
}
