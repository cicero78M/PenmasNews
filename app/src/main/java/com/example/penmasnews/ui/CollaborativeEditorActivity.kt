package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import java.util.Calendar

class CollaborativeEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collaborative_editor)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        dateEdit.setText(prefs.getString("date", ""))
        notesEdit.setText(prefs.getString("notes", ""))

        dateEdit.setOnClickListener {
            showDatePicker(dateEdit)
        }

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
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
}
