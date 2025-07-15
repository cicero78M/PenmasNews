package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageButton
import android.text.Editable
import android.text.TextWatcher
import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.EventStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import java.util.Calendar

class AIHelperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_helper)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val inputEdit = findViewById<EditText>(R.id.editInputText)
        val dasarEdit = findViewById<EditText>(R.id.editDasar)
        val tersangkaEdit = findViewById<EditText>(R.id.editTersangka)
        val tkpEdit = findViewById<EditText>(R.id.editTKP)
        val kronologiEdit = findViewById<EditText>(R.id.editKronologi)
        val modusEdit = findViewById<EditText>(R.id.editModus)
        val barangBuktiEdit = findViewById<EditText>(R.id.editBarangBukti)
        val pasalEdit = findViewById<EditText>(R.id.editPasal)
        val ancamanEdit = findViewById<EditText>(R.id.editAncaman)
        val addColumnButton = findViewById<ImageButton>(R.id.buttonAddColumn)
        val generateButton = findViewById<Button>(R.id.buttonGenerate)
        val outputText = findViewById<TextView>(R.id.textGenerated)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)
        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        val fields = listOf(
            inputEdit,
            dasarEdit,
            tersangkaEdit,
            tkpEdit,
            kronologiEdit,
            modusEdit,
            barangBuktiEdit,
            pasalEdit,
            ancamanEdit,
            notesEdit,
        )
        fun checkReady() {
            val ready = fields.all { it.visibility == android.view.View.VISIBLE && it.text.isNotBlank() }
            generateButton.visibility = if (ready) android.view.View.VISIBLE else android.view.View.GONE
        }
        fields.forEach { field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { checkReady() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
        var currentIndex = 0
        addColumnButton.setOnClickListener {
            if (currentIndex < fields.size) {
                fields[currentIndex].visibility = android.view.View.VISIBLE
                fields[currentIndex].requestFocus()
                currentIndex++
                if (currentIndex >= fields.size) {
                    addColumnButton.visibility = android.view.View.GONE
                }
            }
            checkReady()
        }
        generateButton.visibility = android.view.View.GONE
        saveButton.visibility = android.view.View.GONE

        generateButton.setOnClickListener {
            val apiKey = BuildConfig.OPENAI_API_KEY.ifBlank {
                System.getenv("OPENAI_API_KEY") ?: ""
            }
            if (apiKey.isBlank()) {
                outputText.text = "Missing API key"
                return@setOnClickListener
            }
            val prompt = """
                Anda seorang jurnalis profesional. Berdasarkan informasi di bawah
                ini, tulislah narasi berita berbahasa Indonesia yang mengikuti
                kaidah jurnalistik secara baik, mengalir, lues dan informatif.

                Setelah narasi lengkap, berikan ringkasan singkat dengan diawali
                label 'Ringkasan:'.

                Judul: ${inputEdit.text}
                Dasar: ${dasarEdit.text}
                Tersangka: ${tersangkaEdit.text}
                TKP dan Waktu Kejadian: ${tkpEdit.text}
                Kronologi Penyelidikan dan Penyidikan: ${kronologiEdit.text}
                Modus Operandi: ${modusEdit.text}
                Barang Bukti: ${barangBuktiEdit.text}
                Pasal yang dipersangkakan: ${pasalEdit.text}
                Ancaman hukuman: ${ancamanEdit.text}
                Catatan: ${notesEdit.text}
            """.trimIndent()
            Thread {
                try {
                    val client = OkHttpClient()
                    val obj = JSONObject()
                    obj.put("model", "gpt-4o-mini")
                    val msgs = JSONArray()
                    msgs.put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                    obj.put("messages", msgs)
                    val body = obj.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .header("Authorization", "Bearer $apiKey")
                        .post(body)
                        .build()
                    val response = client.newCall(request).execute()
                    val generated = JSONObject(response.body?.string() ?: "{}")
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    runOnUiThread {
                        outputText.text = generated
                        saveButton.visibility = android.view.View.VISIBLE
                    }
                } catch (e: Exception) {
                    runOnUiThread { outputText.text = "Error: ${e.message}" }
                }
            }.start()
        }

        saveButton.setOnClickListener {
            val events = EventStorage.loadEvents(prefs)
            val generated = outputText.text.toString()
            val parts = generated.split("Ringkasan:")
            val narrative = parts.getOrNull(0)?.trim() ?: ""
            val summary = parts.getOrNull(1)?.trim() ?: ""
            val event = EditorialEvent(
                dateEdit.text.toString(),
                inputEdit.text.toString(),
                "editor",
                "dalam penulisan",
                narrative,
                summary
            )
            events.add(event)
            EventStorage.saveEvents(prefs, events)
            dateEdit.text.clear()
            inputEdit.text.clear()
            dasarEdit.text.clear()
            tersangkaEdit.text.clear()
            tkpEdit.text.clear()
            kronologiEdit.text.clear()
            modusEdit.text.clear()
            barangBuktiEdit.text.clear()
            pasalEdit.text.clear()
            ancamanEdit.text.clear()
            notesEdit.text.clear()
            outputText.text = ""
            generateButton.visibility = android.view.View.GONE
            saveButton.visibility = android.view.View.GONE
            val intent = android.content.Intent(this, EditorialCalendarActivity::class.java)
            startActivity(intent)
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
