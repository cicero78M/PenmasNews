package com.example.penmasnews.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.content.ClipboardManager
import android.content.Context
import android.widget.ImageView
import android.content.Intent
import java.io.File
import android.text.Editable
import android.text.TextWatcher
import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.EventStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import org.json.JSONArray
import org.json.JSONObject
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import java.util.Calendar

class AIHelperActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private var selectedImagePath: String? = null
    companion object { private const val REQUEST_IMAGE = 1001 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_helper)

        val dateEdit = findViewById<EditText>(R.id.editDate)
        val notesEdit = findViewById<EditText>(R.id.editNotes)
        val inputEdit = findViewById<EditText>(R.id.editInputText)
        val layoutInput = findViewById<android.view.View>(R.id.layoutInputText)
        val dasarEdit = findViewById<EditText>(R.id.editDasar)
        val tersangkaEdit = findViewById<EditText>(R.id.editTersangka)
        val tkpEdit = findViewById<EditText>(R.id.editTKP)
        val kronologiEdit = findViewById<EditText>(R.id.editKronologi)
        val modusEdit = findViewById<EditText>(R.id.editModus)
        val barangBuktiEdit = findViewById<EditText>(R.id.editBarangBukti)
        val pasalEdit = findViewById<EditText>(R.id.editPasal)
        val ancamanEdit = findViewById<EditText>(R.id.editAncaman)
        imageView = findViewById(R.id.imageAttachment)

        val pasteDasar = findViewById<ImageButton>(R.id.buttonPasteDasar)
        val clearDasar = findViewById<ImageButton>(R.id.buttonClearDasar)
        val pasteTersangka = findViewById<ImageButton>(R.id.buttonPasteTersangka)
        val clearTersangka = findViewById<ImageButton>(R.id.buttonClearTersangka)
        val pasteTKP = findViewById<ImageButton>(R.id.buttonPasteTKP)
        val clearTKP = findViewById<ImageButton>(R.id.buttonClearTKP)
        val pasteKronologi = findViewById<ImageButton>(R.id.buttonPasteKronologi)
        val clearKronologi = findViewById<ImageButton>(R.id.buttonClearKronologi)
        val pasteModus = findViewById<ImageButton>(R.id.buttonPasteModus)
        val clearModus = findViewById<ImageButton>(R.id.buttonClearModus)
        val pasteBarangBukti = findViewById<ImageButton>(R.id.buttonPasteBarangBukti)
        val clearBarangBukti = findViewById<ImageButton>(R.id.buttonClearBarangBukti)
        val pastePasal = findViewById<ImageButton>(R.id.buttonPastePasal)
        val clearPasal = findViewById<ImageButton>(R.id.buttonClearPasal)
        val pasteAncaman = findViewById<ImageButton>(R.id.buttonPasteAncaman)
        val clearAncaman = findViewById<ImageButton>(R.id.buttonClearAncaman)
        val pasteNotes = findViewById<ImageButton>(R.id.buttonPasteNotes)
        val clearNotes = findViewById<ImageButton>(R.id.buttonClearNotes)

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        fun pasteFromClipboard(target: EditText) {
            val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = cb.primaryClip?.getItemAt(0)?.coerceToText(this)
            if (text != null) target.setText(text)
        }

        pasteDasar.setOnClickListener { pasteFromClipboard(dasarEdit) }
        clearDasar.setOnClickListener { dasarEdit.text.clear() }
        pasteTersangka.setOnClickListener { pasteFromClipboard(tersangkaEdit) }
        clearTersangka.setOnClickListener { tersangkaEdit.text.clear() }
        pasteTKP.setOnClickListener { pasteFromClipboard(tkpEdit) }
        clearTKP.setOnClickListener { tkpEdit.text.clear() }
        pasteKronologi.setOnClickListener { pasteFromClipboard(kronologiEdit) }
        clearKronologi.setOnClickListener { kronologiEdit.text.clear() }
        pasteModus.setOnClickListener { pasteFromClipboard(modusEdit) }
        clearModus.setOnClickListener { modusEdit.text.clear() }
        pasteBarangBukti.setOnClickListener { pasteFromClipboard(barangBuktiEdit) }
        clearBarangBukti.setOnClickListener { barangBuktiEdit.text.clear() }
        pastePasal.setOnClickListener { pasteFromClipboard(pasalEdit) }
        clearPasal.setOnClickListener { pasalEdit.text.clear() }
        pasteAncaman.setOnClickListener { pasteFromClipboard(ancamanEdit) }
        clearAncaman.setOnClickListener { ancamanEdit.text.clear() }
        pasteNotes.setOnClickListener { pasteFromClipboard(notesEdit) }
        clearNotes.setOnClickListener { notesEdit.text.clear() }

        val addColumnButton = findViewById<ImageButton>(R.id.buttonAddColumn)
        val generateButton = findViewById<Button>(R.id.buttonGenerate)
        val progressGenerate = findViewById<android.widget.ProgressBar>(R.id.progressGenerate)
        val titleOutput = findViewById<EditText>(R.id.editSuggestedTitle)
        val narrativeOutput = findViewById<EditText>(R.id.editGeneratedNarrative)
        val summaryOutput = findViewById<EditText>(R.id.editGeneratedSummary)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val prefs = getSharedPreferences(EventStorage.PREFS_NAME, MODE_PRIVATE)
        dateEdit.setOnClickListener { showDatePicker(dateEdit) }

        val layoutDasar = findViewById<android.view.View>(R.id.layoutDasar)
        val layoutTersangka = findViewById<android.view.View>(R.id.layoutTersangka)
        val layoutTKP = findViewById<android.view.View>(R.id.layoutTKP)
        val layoutKronologi = findViewById<android.view.View>(R.id.layoutKronologi)
        val layoutModus = findViewById<android.view.View>(R.id.layoutModus)
        val layoutBarangBukti = findViewById<android.view.View>(R.id.layoutBarangBukti)
        val layoutPasal = findViewById<android.view.View>(R.id.layoutPasal)
        val layoutAncaman = findViewById<android.view.View>(R.id.layoutAncaman)
        val layoutNotes = findViewById<android.view.View>(R.id.layoutNotes)

        val fields = listOf<android.view.View>(
            layoutInput,
            layoutDasar,
            layoutTersangka,
            layoutTKP,
            layoutKronologi,
            layoutModus,
            layoutBarangBukti,
            layoutPasal,
            layoutAncaman,
            layoutNotes,
        )

        val textFields = listOf(
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
            val ready = textFields.all { it.isShown && it.text.isNotBlank() }
            generateButton.visibility = if (ready) android.view.View.VISIBLE else android.view.View.GONE
        }
        textFields.forEach { field ->
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
                textFields[currentIndex].requestFocus()
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
            progressGenerate.visibility = android.view.View.VISIBLE
            generateButton.isEnabled = false
            val apiKey = BuildConfig.OPENAI_API_KEY.ifBlank {
                System.getenv("OPENAI_API_KEY") ?: ""
            }
            if (apiKey.isBlank()) {
                progressGenerate.visibility = android.view.View.GONE
                generateButton.isEnabled = true
                narrativeOutput.setText("Missing API key")
                return@setOnClickListener
            }
            val prompt = """
Anda seorang jurnalis profesional. Berdasarkan informasi berikut ini, buatkan narasi berita dengan struktur sebagai berikut:

   * Tuliskan paragraf pembuka yang mencakup unsur 5W+1H (what, who, when, where, why, how).
   * Jawab inti berita secara ringkas dan jelas.

   * Jelaskan berita secara rinci.
   * Susun informasi menggunakan metode piramida terbalik; mulai dengan informasi yang paling penting terlebih dahulu, diikuti detail pendukung.

   * Sertakan kutipan atau pendapat dari narasumber yang relevan.
   * Pastikan kutipan tersebut memperkuat fakta dalam berita.

   * Tuliskan kalimat penutup singkat yang merangkum inti berita atau memberikan informasi tambahan yang relevan.

Gunakan kalimat pendek, jelas, dan lugas. Hindari jargon atau istilah teknis yang sulit dimengerti pembaca awam. Gunakan bahasa aktif, bukan pasif. Pastikan narasi bersifat faktual, objektif, serta tersusun secara runtut dan mudah dipahami pembaca.

                Berikan saran judul berita baru dengan label 'Judul Baru:' sebelum
                narasi. Setelah narasi lengkap, berikan ringkasan singkat dengan
                diawali label 'Ringkasan:'.

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
                    val client = OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()
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

                    val title = generated.substringAfter("Judul Baru:").substringBefore("\n").trim()
                    val afterTitle = generated.substringAfter("Judul Baru:")
                    val parts = afterTitle.split("Ringkasan:")
                    val narrative = parts.getOrNull(0)?.substringAfter("\n")?.trim() ?: ""
                    val summary = parts.getOrNull(1)?.trim() ?: ""

                    runOnUiThread {
                        progressGenerate.visibility = android.view.View.GONE
                        generateButton.isEnabled = true
                        titleOutput.setText(title)
                        narrativeOutput.setText(narrative)
                        summaryOutput.setText(summary)
                        titleOutput.visibility = android.view.View.VISIBLE
                        narrativeOutput.visibility = android.view.View.VISIBLE
                        summaryOutput.visibility = android.view.View.VISIBLE
                        saveButton.visibility = android.view.View.VISIBLE
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        progressGenerate.visibility = android.view.View.GONE
                        generateButton.isEnabled = true
                        narrativeOutput.setText("Error: ${e.message}")
                        narrativeOutput.visibility = android.view.View.VISIBLE
                    }
                }
            }.start()
        }

        saveButton.setOnClickListener {
            val events = EventStorage.loadEvents(prefs)
            val event = EditorialEvent(
                dateEdit.text.toString(),
                titleOutput.text.toString(),
                "editor",
                "dalam penulisan",
                narrativeOutput.text.toString(),
                summaryOutput.text.toString(),
                selectedImagePath ?: ""
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
            imageView.setImageDrawable(null)
            selectedImagePath = null
            titleOutput.setText("")
            narrativeOutput.setText("")
            summaryOutput.setText("")
            titleOutput.visibility = android.view.View.GONE
            narrativeOutput.visibility = android.view.View.GONE
            summaryOutput.visibility = android.view.View.GONE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            imageView.setImageURI(uri)
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            contentResolver.openInputStream(uri)?.use { input ->
                openFileOutput(fileName, MODE_PRIVATE).use { output ->
                    input.copyTo(output)
                }
            }
            selectedImagePath = File(filesDir, fileName).absolutePath
        }
    }

}
