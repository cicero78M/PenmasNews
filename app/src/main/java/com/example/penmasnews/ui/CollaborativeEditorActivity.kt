package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import java.io.File
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.EventStorage
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.model.ChangeLogStorage
import com.example.penmasnews.ui.ApprovalListActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val assigneeEdit = findViewById<AutoCompleteTextView>(R.id.editAssignee)
        val statusEdit = findViewById<AutoCompleteTextView>(R.id.editStatus)
        imageView = findViewById(R.id.imageCollab)
        logText = findViewById(R.id.textLogs)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val requestButton = findViewById<Button>(R.id.buttonRequestApproval)

        val assigneeList = resources.getStringArray(R.array.assignee_array)
        val statusList = resources.getStringArray(R.array.status_array)
        assigneeEdit.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, assigneeList)
        )
        statusEdit.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        )
        statusEdit.isEnabled = false
        statusEdit.isFocusable = false

        val eventIndex = intent.getIntExtra("index", -1)
        val eventsPrefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)
        val events = EventStorage.loadEvents(eventsPrefs)

        val logPrefs = getSharedPreferences(ChangeLogStorage.PREFS_NAME, MODE_PRIVATE)
        val changeLogs = ChangeLogStorage.loadLogs(logPrefs)
        displayLogs(changeLogs)

        val currentEvent = if (eventIndex in events.indices) events[eventIndex] else null
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
            if (assignee !in assigneeList) {
                Snackbar.make(saveButton, R.string.error_invalid_assignee, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val oldEvent = if (eventIndex in events.indices) events[eventIndex] else null

            if (eventIndex in events.indices) {
                events[eventIndex] = EditorialEvent(
                    currentEvent?.date ?: "",
                    titleEdit.text.toString(),
                    assignee,
                    statusEdit.text.toString(),
                    narrativeEdit.text.toString(),
                    currentEvent?.summary ?: "",
                    imagePath ?: ""
                )
                EventStorage.saveEvents(eventsPrefs, events)
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
            val userPrefs = getSharedPreferences("user", MODE_PRIVATE)
            val user = userPrefs.getString("username", "unknown") ?: "unknown"
            val entry = ChangeLogEntry(user, statusEdit.text.toString(), changesDesc, System.currentTimeMillis())
            changeLogs.add(entry)
            ChangeLogStorage.saveLogs(logPrefs, changeLogs)
            displayLogs(changeLogs)
        }

        requestButton.setOnClickListener {
            val newStatus = "review"
            statusEdit.setText(newStatus)
            if (eventIndex in events.indices) {
                val event = events[eventIndex]
                event.status = newStatus
                events[eventIndex] = event
                EventStorage.saveEvents(eventsPrefs, events)
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
        logText.text = logs.joinToString("\n") {
            "${df.format(Date(it.timestamp))} - ${it.user} - ${it.status} - ${it.changes}"
        }
    }
}
