package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Spinner
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.network.LogService
import com.example.penmasnews.feature.CMSIntegration
import com.example.penmasnews.feature.BloggerAuth
import com.example.penmasnews.util.DebugLogger
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
    private val RC_WP_LOGIN = 9002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editorial_calendar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val topicSpinner = findViewById<android.widget.Spinner>(R.id.spinnerTopic)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
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
                if (com.example.penmasnews.BuildConfig.BLOGGER_CLIENT_ID.isBlank()) {
                    Toast.makeText(this, "Blogger CLIENT_ID belum diatur", Toast.LENGTH_LONG).show()
                    DebugLogger.log(this, "BLOGGER_CLIENT_ID is blank")
                    return@EditorialCalendarAdapter
                }
                val stored = com.example.penmasnews.model.CMSPrefs.getBloggerToken(this)
                if (stored != null) {
                    publishEvent(event, stored)
                } else {
                    pendingPublish = event
                    BloggerAuth.startLogin(this) { token ->
                        runOnUiThread {
                            if (token != null) {
                                com.example.penmasnews.model.CMSPrefs.saveBloggerToken(this, token)
                                publishEvent(event, token)
                            } else {
                                Toast.makeText(this, "Login gagal", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            },
            onPublishWordpress = { event, _ ->
                val token = com.example.penmasnews.model.CMSPrefs.getWordpressToken(this)
                if (token != null) {
                    publishEventWordpress(event)
                } else {
                    pendingPublish = event
                    startActivityForResult(
                        android.content.Intent(this, WordpressLoginActivity::class.java),
                        RC_WP_LOGIN
                    )
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
                topicSpinner.selectedItem.toString(),
                assignee,
                status,
                notesEdit.text.toString(),
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
                    notesEdit.text.clear()
                    topicSpinner.setSelection(0)
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

    private fun publishEvent(event: EditorialEvent, token: String) {
        DebugLogger.log(this, "Publishing event '${'$'}{event.topic}' via Blogger API")
        val cms = CMSIntegration(this)
        Thread {
            val result = cms.publishToBlogspot(event, token)
            val prefsAuth = getSharedPreferences("auth", MODE_PRIVATE)
            val authToken = prefsAuth.getString("token", null)
            val user = prefsAuth.getString("username", "") ?: ""
            if (result.success && authToken != null) {
                val updated = event.copy(
                    status = "published",
                    lastUpdate = DateUtils.now(),
                    updatedBy = user
                )
                EventService.updateEvent(authToken, event.id, updated)
            }
            runOnUiThread {
                val fallback = if (result.success) "Dipublikasikan" else "Gagal publish"
                val msg = result.raw ?: fallback
                DebugLogger.log(this, "Publish result: ${'$'}msg")
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("publish_response", msg))
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_copy) {
                        clipboard.setPrimaryClip(ClipData.newPlainText("publish_response", msg))
                    }
                    .show()
            }
        }.start()
    }

    private fun publishEventWordpress(event: EditorialEvent) {
        DebugLogger.log(this, "Publishing event '${'$'}{event.topic}' via WordPress API")
        val cms = CMSIntegration(this)
        Thread {
            val result = cms.publishToWordpress(event)
            val prefsAuth = getSharedPreferences("auth", MODE_PRIVATE)
            val authToken = prefsAuth.getString("token", null)
            val user = prefsAuth.getString("username", "") ?: ""
            if (result.success && authToken != null) {
                val updated = event.copy(
                    status = "published",
                    lastUpdate = DateUtils.now(),
                    updatedBy = user
                )
                EventService.updateEvent(authToken, event.id, updated)
            }
            runOnUiThread {
                val fallback = if (result.success) "Dipublikasikan" else "Gagal publish"
                val msg = result.raw ?: fallback
                DebugLogger.log(this, "WordPress publish result: ${'$'}msg")
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("publish_response", msg))
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_copy) {
                        clipboard.setPrimaryClip(ClipData.newPlainText("publish_response", msg))
                    }
                    .show()
            }
        }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BloggerAuth.RC_SIGN_IN) {
            BloggerAuth.handleAuthResponse(this, data) { token ->
                pendingPublish?.let { event ->
                    if (token != null) {
                        com.example.penmasnews.model.CMSPrefs.saveBloggerToken(this, token)
                        publishEvent(event, token)
                    } else {
                        Toast.makeText(this, "Login gagal", Toast.LENGTH_LONG).show()
                    }
                }
                pendingPublish = null
            }
        } else if (requestCode == RC_WP_LOGIN && resultCode == RESULT_OK) {
            pendingPublish?.let { publishEventWordpress(it) }
            pendingPublish = null
        }
    }

    // persistence handled by EventStorage
}
