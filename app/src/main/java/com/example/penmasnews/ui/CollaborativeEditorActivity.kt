package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    }
}
