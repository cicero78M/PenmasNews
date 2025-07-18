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
import com.example.penmasnews.model.ChangeLogDatabase
import com.example.penmasnews.ui.ApprovalListActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDateTime

class CollaborativeEditorActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var logText: TextView
    private var imagePath: String? = null
    companion object { private const val REQUEST_IMAGE = 2001 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collaborative_editor)

        val titleEdit = findViewById<EditText>(R.id.editTitle)
        val narrativeEdit = findViewById<EditText>(R.id.editNarrative)
        val assigneeEdit = findViewById<EditText>(R.id.editAssignee)
        val statusEdit = findViewById<EditText>(R.id.editStatus)
        imageView = findViewById(R.id.imageCollab)
        logText = findViewById(R.id.textLogs)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val requestButton = findViewById<Button>(R.id.buttonRequestApproval)


        val passedEvent = intent.getSerializableExtra("event") as? EditorialEvent
        val eventsPrefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)
        val events = EventStorage.loadEvents(this)
        val eventIndex = passedEvent?.let { evt -> events.indexOfFirst { it.id == evt.id } } ?: intent.getIntExtra("index", -1)

        val changeLogs = ChangeLogDatabase.getLogs(this)
        displayLogs(changeLogs)

        val currentEvent = if (eventIndex in events.indices) events[eventIndex] else passedEvent
        imagePath = currentEvent?.imagePath
        imagePath?.let { path ->
            if (path.isNotBlank()) imageView.setImageURI(android.net.Uri.fromFile(File(path)))
        }
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        titleEdit.setText(currentEvent?.topic ?: "")
        narrativeEdit.setText(currentEvent?.content ?: "")
        assigneeEdit.setText(currentEvent?.assignee ?: "")
        statusEdit.setText(currentEvent?.status ?: "")

        saveButton.setOnClickListener {
            val assignee = assigneeEdit.text.toString()
            val oldEvent = if (eventIndex in events.indices) events[eventIndex] else null

            if (eventIndex in events.indices) {
                val updated = EditorialEvent(
                    currentEvent?.date ?: "",
                    titleEdit.text.toString(),
                    assignee,
                    statusEdit.text.toString(),
                    narrativeEdit.text.toString(),
                    currentEvent?.summary ?: "",
                    imagePath ?: "",
                    events[eventIndex].id,
                    currentEvent?.createdAt ?: "",
                    LocalDateTime.now().toString(),
                    currentEvent?.username ?: ""
                )
                if (EventStorage.updateEvent(this, updated)) {
                    events[eventIndex] = updated
                }
            }

            val oldTitle = oldEvent?.topic ?: ""
            val oldContent = oldEvent?.content ?: ""
            val oldAssignee = oldEvent?.assignee ?: ""
            val oldStatus = oldEvent?.status ?: ""
            val oldImage = oldEvent?.imagePath ?: ""

            val changed = mutableListOf<String>()
            if (oldTitle != titleEdit.text.toString()) changed.add("title")
            if (oldContent != narrativeEdit.text.toString()) changed.add("content")
            if (oldAssignee != assigneeEdit.text.toString()) changed.add("assignee")
            if (oldStatus != statusEdit.text.toString()) changed.add("status")
            if (oldImage != (imagePath ?: "")) changed.add("image")

            val changesDesc = if (changed.isEmpty()) "no change" else changed.joinToString(", ")
            val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
            val user = authPrefs.getString("username", "unknown") ?: "unknown"
            val entry = ChangeLogEntry(user, statusEdit.text.toString(), changesDesc, System.currentTimeMillis() / 1000L)
            ChangeLogDatabase.addLog(this, entry)
            displayLogs(ChangeLogDatabase.getLogs(this))
        }

        requestButton.setOnClickListener {
            val newStatus = "review"
            statusEdit.setText(newStatus)
            if (eventIndex in events.indices) {
                val event = events[eventIndex]
                val updated = event.copy(status = newStatus)
                if (EventStorage.updateEvent(this, updated)) {
                    events[eventIndex] = updated
                }
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
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        df.timeZone = java.util.TimeZone.getTimeZone("Asia/Jakarta")
        logText.text = logs.joinToString("\n") {
            val date = Date(it.timestamp * 1000)
            "${df.format(date)} - ${it.user} - ${it.status} - ${it.changes}"
        }
    }
}
