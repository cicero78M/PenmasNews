package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import java.util.Calendar
import java.io.BufferedReader
import java.io.InputStreamReader

class AIHelperActivity : AppCompatActivity() {
    private val pickDoc = 100
    private val pickPdf = 101
    private val pickImage = 102
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_helper)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val inputEdit = findViewById<EditText>(R.id.editInputText)
        val docText = findViewById<TextView>(R.id.textDoc)
        val pdfText = findViewById<TextView>(R.id.textPdf)
        val imageText = findViewById<TextView>(R.id.textImage)
        val docButton = findViewById<Button>(R.id.buttonChooseDoc)
        val pdfButton = findViewById<Button>(R.id.buttonChoosePdf)
        val imageButton = findViewById<Button>(R.id.buttonChooseImage)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        dateEdit.setText(prefs.getString("date", ""))
        notesEdit.setText(prefs.getString("notes", ""))
        inputEdit.setText(prefs.getString("input", ""))
        docText.text = prefs.getString("doc", getString(R.string.label_no_file))
        pdfText.text = prefs.getString("pdf", getString(R.string.label_no_file))
        imageText.text = prefs.getString("image", getString(R.string.label_no_file))

        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        docButton.setOnClickListener { pickDocFile() }
        pdfButton.setOnClickListener { pickFile("application/pdf", pickPdf) }
        imageButton.setOnClickListener { pickFile("image/*", pickImage) }

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
                .putString("input", inputEdit.text.toString())
                .putString("doc", docText.text.toString())
                .putString("pdf", pdfText.text.toString())
                .putString("image", imageText.text.toString())
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

    private fun pickFile(type: String, request: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { this.type = type }
        startActivityForResult(intent, request)
    }

    private fun pickDocFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            )
        }
        startActivityForResult(intent, pickDoc)
    }

    private fun readFileText(uri: Uri): String {
        return try {
            contentResolver.openInputStream(uri)?.use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            } ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val uri = data?.data ?: return
        val name = uri.lastPathSegment ?: uri.toString()
        when (requestCode) {
            pickDoc -> {
                findViewById<TextView>(R.id.textDoc).text = name
                val mime = contentResolver.getType(uri)
                val text = if (mime == "application/msword") readFileText(uri) else ""
                findViewById<EditText>(R.id.editInputText).setText(text)
            }
            pickPdf -> findViewById<TextView>(R.id.textPdf).text = name
            pickImage -> findViewById<TextView>(R.id.textImage).text = name
        }
    }
}
