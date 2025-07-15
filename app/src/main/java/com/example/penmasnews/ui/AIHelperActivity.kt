package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.penmasnews.BuildConfig
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
        val generateButton = findViewById<Button>(R.id.buttonGenerate)
        val outputText = findViewById<TextView>(R.id.textGenerated)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(javaClass.simpleName, MODE_PRIVATE)

        dateEdit.setText(prefs.getString("date", ""))
        notesEdit.setText(prefs.getString("notes", ""))
        inputEdit.setText(prefs.getString("input", ""))
        dasarEdit.setText(prefs.getString("dasar", ""))
        tersangkaEdit.setText(prefs.getString("tersangka", ""))
        tkpEdit.setText(prefs.getString("tkp", ""))
        kronologiEdit.setText(prefs.getString("kronologi", ""))
        modusEdit.setText(prefs.getString("modus", ""))
        barangBuktiEdit.setText(prefs.getString("barang_bukti", ""))
        pasalEdit.setText(prefs.getString("pasal", ""))
        ancamanEdit.setText(prefs.getString("ancaman", ""))
        outputText.text = prefs.getString("generated", "")
        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        generateButton.setOnClickListener {
            val apiKey = BuildConfig.OPENAI_API_KEY.ifBlank {
                System.getenv("OPENAI_API_KEY") ?: ""
            }
            if (apiKey.isBlank()) {
                outputText.text = "Missing API key"
                return@setOnClickListener
            }
            val prompt = "Judul: ${inputEdit.text}\nDasar: ${dasarEdit.text}\nTersangka: ${tersangkaEdit.text}\nTKP: ${tkpEdit.text}\nKronologi: ${kronologiEdit.text}\nModus: ${modusEdit.text}\nBarang Bukti: ${barangBuktiEdit.text}\nPasal: ${pasalEdit.text}\nAncaman: ${ancamanEdit.text}"
            Thread {
                try {
                    val client = OkHttpClient()
                    val obj = JSONObject()
                    obj.put("model", "gpt-3.5-turbo")
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
                    val generated = JSONObject(response.body?.string() ?: "{}").getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                    runOnUiThread { outputText.text = generated }
                } catch (e: Exception) {
                    runOnUiThread { outputText.text = "Error: ${e.message}" }
                }
            }.start()
        }

        saveButton.setOnClickListener {
            prefs.edit()
                .putString("date", dateEdit.text.toString())
                .putString("notes", notesEdit.text.toString())
                .putString("input", inputEdit.text.toString())
                .putString("dasar", dasarEdit.text.toString())
                .putString("tersangka", tersangkaEdit.text.toString())
                .putString("tkp", tkpEdit.text.toString())
                .putString("kronologi", kronologiEdit.text.toString())
                .putString("modus", modusEdit.text.toString())
                .putString("barang_bukti", barangBuktiEdit.text.toString())
                .putString("pasal", pasalEdit.text.toString())
                .putString("ancaman", ancamanEdit.text.toString())
                .putString("generated", outputText.text.toString())
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
