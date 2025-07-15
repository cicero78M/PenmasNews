package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.ApprovalStorage
import com.example.penmasnews.ui.ApprovalListActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R

class CollaborativeEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collaborative_editor)

        val titleEdit = findViewById<EditText>(R.id.editTitle)
        val narrativeEdit = findViewById<EditText>(R.id.editNarrative)
        val assigneeEdit = findViewById<EditText>(R.id.editAssignee)
        val statusEdit = findViewById<EditText>(R.id.editStatus)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val requestButton = findViewById<Button>(R.id.buttonRequestApproval)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        titleEdit.setText(intent.getStringExtra("title") ?: prefs.getString("title", ""))
        narrativeEdit.setText(intent.getStringExtra("content") ?: prefs.getString("content", ""))
        assigneeEdit.setText(intent.getStringExtra("assignee") ?: prefs.getString("assignee", ""))
        statusEdit.setText(intent.getStringExtra("status") ?: prefs.getString("status", ""))

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("title", titleEdit.text.toString())
                .putString("content", narrativeEdit.text.toString())
                .putString("assignee", assigneeEdit.text.toString())
                .putString("status", statusEdit.text.toString())
                .apply()
        }

        requestButton.setOnClickListener {
            val prefsApproval = getSharedPreferences(ApprovalStorage.PREFS_NAME, MODE_PRIVATE)
            val approvals = ApprovalStorage.loadEvents(prefsApproval)
            approvals.add(
                EditorialEvent(
                    "",
                    titleEdit.text.toString(),
                    assigneeEdit.text.toString(),
                    statusEdit.text.toString(),
                    narrativeEdit.text.toString()
                )
            )
            ApprovalStorage.saveEvents(prefsApproval, approvals)
            startActivity(Intent(this, ApprovalListActivity::class.java))
        }
    }
}
