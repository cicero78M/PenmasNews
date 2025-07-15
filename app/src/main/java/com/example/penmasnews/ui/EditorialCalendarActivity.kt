package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent
import java.util.Calendar

class EditorialCalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editorial_calendar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCalendar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        dateEdit.setText(prefs.getString("date", ""))
        notesEdit.setText(prefs.getString("notes", ""))

        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
                .apply()
        }

        // Sample data to illustrate the calendar design
        val events = listOf(
            EditorialEvent("1 Jan", "Refleksi Awal Tahun", "Andi", "draft"),
            EditorialEvent("5 Jan", "Tren Teknologi 2024", "Budi", "review"),
            EditorialEvent("10 Jan", "Wawancara Tokoh", "Citra", "publish")
        )

        recyclerView.adapter = EditorialCalendarAdapter(events)
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
