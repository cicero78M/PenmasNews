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
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.model.ChangeLogStorage
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
        val typeSpinner = findViewById<android.widget.Spinner>(R.id.spinnerType)
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

        val index = intent.getIntExtra("index", -1)
        val extrasDate = intent.getStringExtra("date")
        val extrasTitle = intent.getStringExtra("title")
        extrasDate?.let { dateEdit.setText(it) }
        extrasTitle?.let { inputEdit.setText(it) }

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
        val layoutSuggestedTitle = findViewById<android.view.View>(R.id.layoutSuggestedTitle)
        val layoutGeneratedNarrative = findViewById<android.view.View>(R.id.layoutGeneratedNarrative)
        val layoutGeneratedSummary = findViewById<android.view.View>(R.id.layoutGeneratedSummary)
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

        val allViews = listOf(
            layoutInput,
            typeSpinner,
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

        val pressFields = listOf(
            layoutInput,
            typeSpinner,
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

        val pressText = listOf(
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

        val pressFocus = listOf<android.view.View>(
            inputEdit,
            typeSpinner,
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

        val onlineFields = listOf(
            layoutInput,
            typeSpinner,
            layoutNotes,
        )

        val onlineText = listOf(
            inputEdit,
            notesEdit,
        )

        val onlineFocus = listOf<android.view.View>(
            inputEdit,
            typeSpinner,
            notesEdit,
        )

        var fields = pressFields
        var textFields = pressText
        var focusFields = pressFocus
        var isPressRelease = true
        var currentIndex = 0

        fun checkReady() {
            val ready = textFields.all { it.isShown && it.text.isNotBlank() }
            generateButton.visibility = if (ready) android.view.View.VISIBLE else android.view.View.GONE
        }

        fun updateForType(press: Boolean) {
            isPressRelease = press
            fields = if (press) pressFields else onlineFields
            textFields = if (press) pressText else onlineText
            focusFields = if (press) pressFocus else onlineFocus
            allViews.forEach { view ->
                if (view !in listOf(layoutInput, typeSpinner)) {
                    view.visibility = android.view.View.GONE
                }
            }
            currentIndex = listOf(layoutInput, typeSpinner).count { it.isShown }
            addColumnButton.visibility = if (currentIndex < fields.size) android.view.View.VISIBLE else android.view.View.GONE
            checkReady()
        }

        typeSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateForType(position == 0)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        updateForType(true)
        textFields.forEach { field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { checkReady() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
        addColumnButton.setOnClickListener {
            if (currentIndex < fields.size) {
                fields[currentIndex].visibility = android.view.View.VISIBLE
                focusFields[currentIndex].requestFocus()
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
            val prompt = if (isPressRelease) """
                Anda seorang jurnalis profesional. Berdasarkan informasi berikut ini

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
                
                buatkan narasi berita dengan struktur sebagai berikut:

                Tuliskan paragraf pembuka yang mencakup unsur 5W+1H (what, who, when, where, why, how).
                Jawab inti berita secara ringkas dan jelas.

                Jelaskan berita secara rinci.
                Susun informasi menggunakan metode piramida terbalik; mulai dengan informasi yang paling penting terlebih dahulu, diikuti detail pendukung.

                Sertakan kutipan atau pendapat dari narasumber yang relevan, dengan kalimat wajar, tidak berlebihan.
                Pastikan kutipan tersebut memperkuat fakta dalam berita.

                Tuliskan kalimat penutup singkat yang merangkum inti berita atau memberikan informasi tambahan yang relevan.

                Gunakan kalimat pendek, jelas, dan lugas. 
                Hindari jargon atau istilah teknis yang sulit dimengerti pembaca awam. 
                Gunakan bahasa aktif, bukan pasif. 
                Pastikan narasi bersifat faktual, objektif, serta tersusun secara runtut dan mudah dipahami pembaca.

                Berikan saran judul berita baru dengan label 'Judul Baru:' sebelum
                narasi. label 'Narasi:' untuk isi berita dan berikan ringkasan singkat dengan
                diawali label 'Ringkasan:'.

               
            """.trimIndent() else """
                
                Anda seorang jurnalis profesional. 
                Berdasarkan catatan berikut, buatkan narasi berita dengan struktur :

                Tuliskan paragraf pembuka yang mencakup unsur 5W+1H (what, who, when, where, why, how).
                Jawab inti berita secara ringkas dan jelas.
                
                Jelaskan berita secara rinci.
                Susun informasi menggunakan metode piramida terbalik; mulai dengan informasi yang paling penting terlebih dahulu, diikuti detail pendukung.
                
                Sertakan kutipan atau pendapat dari narasumber yang relevan, dengan kalimat wajar, tidak berlebihan.
                Pastikan kutipan tersebut memperkuat fakta dalam berita.
                
                Tuliskan kalimat penutup singkat yang merangkum inti berita atau memberikan informasi tambahan yang relevan.
                
                Gunakan kalimat pendek, jelas, dan lugas. 
                Hindari jargon atau istilah teknis yang sulit dimengerti pembaca awam. 
                Gunakan bahasa aktif, bukan pasif. 
                Pastikan narasi bersifat faktual, objektif, serta tersusun secara runtut dan mudah dipahami pembaca.

                Tulis judul baru dengan label 'Judul Baru:' kemudian 'Narasi:' dan 'Ringkasan:'.

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

                    val title = generated.substringAfter("**Judul Baru:**").substringBefore("\n").trim()
                    val afterTitle = generated.substringAfter("**Judul Baru:**")
                    val narrative = afterTitle
                        .substringAfter("**Narasi:**")
                        .substringBefore("**Ringkasan:**")
                        .trim()
                    val summary = afterTitle.substringAfter("**Ringkasan:**").trim()

                    runOnUiThread {
                        progressGenerate.visibility = android.view.View.GONE
                        generateButton.isEnabled = true
                        titleOutput.setText(title)
                        narrativeOutput.setText(narrative)
                        summaryOutput.setText(summary)
                        layoutSuggestedTitle.visibility = android.view.View.VISIBLE
                        layoutGeneratedNarrative.visibility = android.view.View.VISIBLE
                        layoutGeneratedSummary.visibility = android.view.View.VISIBLE
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
            if (index >= 0 && index < events.size) {
                events[index] = event
            } else {
                events.add(event)
            }
            EventStorage.saveEvents(prefs, events)
            // log save of AI generated content
            val logPrefs = getSharedPreferences(ChangeLogStorage.PREFS_NAME, MODE_PRIVATE)
            val logs = ChangeLogStorage.loadLogs(logPrefs)
            val userPrefs = getSharedPreferences("user", MODE_PRIVATE)
            val user = userPrefs.getString("username", "unknown") ?: "unknown"
            val changesDesc = listOf("ai_generated", "date", "title").joinToString(", ")
            logs.add(
                ChangeLogEntry(
                    user,
                    event.status,
                    changesDesc,
                    System.currentTimeMillis()
                )
            )
            ChangeLogStorage.saveLogs(logPrefs, logs)
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
            layoutSuggestedTitle.visibility = android.view.View.GONE
            layoutGeneratedNarrative.visibility = android.view.View.GONE
            layoutGeneratedSummary.visibility = android.view.View.GONE
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
