package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.content.Intent
import java.io.File
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.ApprovalStorage
import com.example.penmasnews.ui.ApprovalListActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R

class CollaborativeEditorActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
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
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val requestButton = findViewById<Button>(R.id.buttonRequestApproval)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        imagePath = prefs.getString("imagePath", null)
        imagePath?.let { path ->
            if (path.isNotBlank()) imageView.setImageURI(android.net.Uri.fromFile(File(path)))
        }
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

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
                .putString("imagePath", imagePath ?: "")
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
                    narrativeEdit.text.toString(),
                    "",
                    imagePath ?: ""
                )
            )
            ApprovalStorage.saveEvents(prefsApproval, approvals)
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
}
