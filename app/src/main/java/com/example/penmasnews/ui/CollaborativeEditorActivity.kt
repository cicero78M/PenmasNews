package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import java.io.File
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.network.LogService
import com.example.penmasnews.network.ApprovalService
import com.example.penmasnews.ui.ApprovalListActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.util.DateUtils

class CollaborativeEditorActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var logText: TextView
    private var imagePath: String? = null
    companion object { private const val REQUEST_IMAGE = 2001 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collaborative_editor)

        val titleEdit = findViewById<EditText>(R.id.editTitle)
        val topicText = findViewById<TextView>(R.id.textTopic)
        val narrativeEdit = findViewById<EditText>(R.id.editNarrative)
        val assigneeEdit = findViewById<EditText>(R.id.editAssignee)
        val tagEdit = findViewById<EditText>(R.id.editTag)
        val categoryEdit = findViewById<EditText>(R.id.editCategory)
        imageView = findViewById(R.id.imageCollab)
        logText = findViewById(R.id.textLogs)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val progressUpload = findViewById<android.widget.ProgressBar>(R.id.progressUpload)
        val requestButton = findViewById<Button>(R.id.buttonRequestApproval)


        val passedEvent = intent.getSerializableExtra("event") as? EditorialEvent

        // Load events list in background for later updates
        val events = mutableListOf<EditorialEvent>()
        var eventIndex = -1
        Thread {
            val loaded = EventStorage.loadEvents(this)
            val idx = passedEvent?.let { evt -> loaded.indexOfFirst { it.id == evt.id } } ?: -1
            runOnUiThread {
                events.addAll(loaded)
                eventIndex = idx
            }
        }.start()

        val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = authPrefs.getString("token", null)
        val changeLogs = mutableListOf<ChangeLogEntry>()
        val currentId = passedEvent?.id ?: -1
        if (token != null && currentId != -1) {
            Thread {
                val logs = LogService.fetchLogs(token, currentId)
                changeLogs.addAll(logs)
                runOnUiThread { displayLogs(changeLogs) }
            }.start()
        } else {
            displayLogs(changeLogs)
        }

        val currentEvent = if (eventIndex in events.indices) events[eventIndex] else passedEvent
        imagePath = currentEvent?.imagePath
        imagePath?.let { path ->
            if (path.isNotBlank()) {
                if (path.startsWith("http")) {
                    Thread {
                        try {
                            val bmp = android.graphics.BitmapFactory.decodeStream(java.net.URL(path).openStream())
                            runOnUiThread { imageView.setImageBitmap(bmp) }
                        } catch (_: Exception) { }
                    }.start()
                } else {
                    imageView.setImageURI(android.net.Uri.fromFile(File(path)))
                }
            }
        }
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        topicText.text = "Topik: ${currentEvent?.topic ?: ""}"
        titleEdit.setText(currentEvent?.title ?: "")
        narrativeEdit.setText(currentEvent?.content ?: "")
        assigneeEdit.setText(currentEvent?.assignee ?: "")
        tagEdit.setText(currentEvent?.tag ?: "")
        categoryEdit.setText(currentEvent?.category ?: "")

        saveButton.setOnClickListener {
            val assignee = assigneeEdit.text.toString()
            val oldEvent = if (eventIndex in events.indices) events[eventIndex] else passedEvent

            val eventId = if (eventIndex in events.indices) events[eventIndex].id else passedEvent?.id ?: 0
            val isNewImage = oldEvent?.imagePath != imagePath && !(imagePath ?: "").startsWith("http")
            if (isNewImage) {
                progressUpload.visibility = android.view.View.VISIBLE
                saveButton.isEnabled = false
            }
            if (eventId != 0) {
                val updated = EditorialEvent(
                    currentEvent?.date ?: "",
                    currentEvent?.topic ?: "",
                    titleEdit.text.toString(),
                    assignee,
                    "dalam penulisan",
                    narrativeEdit.text.toString(),
                    currentEvent?.summary ?: "",
                    imagePath ?: "",
                    tagEdit.text.toString(),
                    categoryEdit.text.toString(),
                    eventId,
                    currentEvent?.createdAt ?: "",
                    DateUtils.now(),
                    currentEvent?.username ?: "",
                    authPrefs.getString("userId", currentEvent?.updatedBy ?: "") ?: currentEvent?.updatedBy ?: ""
                )
                Thread {
                    val success = EventStorage.updateEvent(this, updated)
                    runOnUiThread {
                        if (isNewImage) {
                            progressUpload.visibility = android.view.View.GONE
                            saveButton.isEnabled = true
                        }
                        if (success && eventIndex in events.indices) {
                            events[eventIndex] = updated
                        }
                    }
                }.start()
            } else if (isNewImage) {
                progressUpload.visibility = android.view.View.GONE
                saveButton.isEnabled = true
            }

            val oldTitle = oldEvent?.topic ?: ""
            val oldContent = oldEvent?.content ?: ""
            val oldAssignee = oldEvent?.assignee ?: ""
            val oldImage = oldEvent?.imagePath ?: ""
            val oldTag = oldEvent?.tag ?: ""
            val oldCategory = oldEvent?.category ?: ""

            val changed = mutableListOf<String>()
            if (oldTitle != titleEdit.text.toString()) changed.add("title")
            if (oldContent != narrativeEdit.text.toString()) changed.add("content")
            if (oldAssignee != assigneeEdit.text.toString()) changed.add("assignee")
            if (oldImage != (imagePath ?: "")) changed.add("image")
            if (oldTag != tagEdit.text.toString()) changed.add("tag")
            if (oldCategory != categoryEdit.text.toString()) changed.add("category")

            val changesDesc = if (changed.isEmpty()) "no change" else changed.joinToString(", ")
            val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
            val token = authPrefs.getString("token", null)
            val username = authPrefs.getString("username", "unknown") ?: "unknown"
            val entry = ChangeLogEntry(username, "dalam penulisan", changesDesc, System.currentTimeMillis() / 1000L)
            val evId = currentEvent?.id ?: 0
            if (token != null && evId != 0) {
                Thread {
                    LogService.addLog(token, evId, entry)
                    val logs = LogService.fetchLogs(token, evId)
                    runOnUiThread { displayLogs(logs) }
                }.start()
            }
        }

        requestButton.setOnClickListener {
            val newStatus = "meminta persetujuan"
            val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
            val token = authPrefs.getString("token", null)
            var evId = passedEvent?.id ?: 0
            if (eventIndex in events.indices) evId = events[eventIndex].id

            if (evId != 0) {
                val baseEvent = if (eventIndex in events.indices) events[eventIndex] else passedEvent!!
                val updated = baseEvent.copy(
                    status = newStatus,
                    lastUpdate = DateUtils.now(),
                    updatedBy = authPrefs.getString("userId", baseEvent.updatedBy) ?: baseEvent.updatedBy
                )
                Thread {
                    EventStorage.updateEvent(this, updated)
                    if (token != null) {
                        ApprovalService.createApproval(token, evId)
                    }
                    if (eventIndex in events.indices) {
                        events[eventIndex] = updated
                    }
                }.start()
            }
            Snackbar.make(requestButton, R.string.status_changed_review, Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this, ApprovalListActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            imageView.setImageURI(uri)
            val fileName = "collab_${System.currentTimeMillis()}.jpg"
            contentResolver.openInputStream(uri)?.use { input ->
                openFileOutput(fileName, MODE_PRIVATE).use { output ->
                    input.copyTo(output)
                }
            }
            imagePath = File(filesDir, fileName).absolutePath
        }
    }

    private fun displayLogs(logs: List<ChangeLogEntry>) {
        logText.text = logs.joinToString("\n") {
            val ts = DateUtils.formatTimestamp(it.timestamp)
            "$ts - ${it.user} - ${it.status} - ${it.changes}"
        }
    }
}
