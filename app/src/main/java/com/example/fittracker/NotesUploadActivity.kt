package com.example.fittracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.NoteEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityGenericFormBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Doubles as Create and Edit. Pass [EXTRA_NOTE_ID] to load an existing note
 * and run Submit as an update; omit it to insert a new note.
 */
class NotesUploadActivity : UniSyncActivity() {

    private lateinit var binding: ActivityGenericFormBinding
    private var pickedImage: Uri? = null
    private var editingId: Long = -1L
    private var editingExistingImageUri: String? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* not persistable */ }
            pickedImage = uri
            binding.formImagePicker.setImageURI(uri)
            Toast.makeText(this, "Image attached", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenericFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.formField2.hint = getString(R.string.field_category)
        binding.formField3.visibility = View.GONE
        binding.formField4.visibility = View.GONE

        binding.formImagePicker.setOnClickListener { pickImage.launch(arrayOf("image/*")) }
        binding.formSubmit.setOnClickListener { save() }
        binding.formCancel.setOnClickListener { finish() }

        editingId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (editingId != -1L) {
            binding.header.headerTitle.text = "Edit Note"
            binding.formSubmit.setText(R.string.action_update)
            loadExisting(editingId)
        } else {
            binding.header.headerTitle.setText(R.string.notes_upload)
        }
    }

    private fun loadExisting(id: Long) {
        lifecycleScope.launch {
            val note = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao().byId(id)
            }
            if (note == null) {
                Toast.makeText(this@NotesUploadActivity, "Note not found", Toast.LENGTH_SHORT).show()
                finish(); return@launch
            }
            binding.formTitle.setText(note.title)
            binding.formField2.setText(note.category)
            binding.formFieldDescription.setText(note.body)
            editingExistingImageUri = note.imageUri
            note.imageUri?.let { uriString ->
                try {
                    binding.formImagePicker.setImageURI(Uri.parse(uriString))
                } catch (_: Exception) { /* placeholder */ }
            }
        }
    }

    private fun save() {
        val title = binding.formTitle.text?.toString()?.trim().orEmpty()
        if (title.isEmpty()) {
            binding.formTitle.error = "Title required"; return
        }
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }
        val category = binding.formField2.text?.toString().orEmpty()
        val body = binding.formFieldDescription.text?.toString().orEmpty()
        val imageUri = pickedImage?.toString() ?: editingExistingImageUri

        lifecycleScope.launch {
            val dao = AppDatabase.get(applicationContext).noteDao()
            if (editingId != -1L) {
                val existing = withContext(Dispatchers.IO) { dao.byId(editingId) }
                if (existing != null) {
                    withContext(Dispatchers.IO) {
                        dao.update(
                            existing.copy(
                                title = title,
                                category = category,
                                subtitle = category,
                                body = body,
                                imageUri = imageUri,
                            )
                        )
                    }
                    Toast.makeText(this@NotesUploadActivity, "Note updated", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.IO) {
                    dao.insert(
                        NoteEntity(
                            ownerId = ownerId,
                            title = title,
                            subtitle = category,
                            body = body,
                            category = category,
                            imageUri = imageUri,
                        )
                    )
                }
                Toast.makeText(this@NotesUploadActivity, "Note saved", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
