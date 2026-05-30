package com.example.fittracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.NoteEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityCreateDesignBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateDesignActivity : UniSyncActivity() {

    private lateinit var binding: ActivityCreateDesignBinding
    private var pageCount: Int = 1
    private var attachedImage: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* not persistable */ }
            attachedImage = uri
            binding.attachedImagePreview.visibility = android.view.View.VISIBLE
            binding.attachedImagePreview.setImageURI(uri)
            Toast.makeText(this, "Image attached to note", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.addNewPage.setOnClickListener {
            pageCount += 1
            binding.docLabel.text = "Document $pageCount"
            Toast.makeText(this, "Page $pageCount added", Toast.LENGTH_SHORT).show()
        }
        binding.saveButton.setOnClickListener { saveNote() }

        // Left rail icons: the 3rd one (gallery) opens the image picker
        (binding.railLeft.getChildAt(2) as? android.widget.ImageView)?.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }
    }

    override fun onBottomNavCenterClicked() {
        // Already creating a note — no-op
    }

    private fun saveNote() {
        val title = binding.inputTitle.text?.toString()?.trim().orEmpty()
        val body = binding.inputBody.text?.toString().orEmpty()
        if (title.isEmpty()) {
            binding.inputTitle.error = "Title required"; return
        }
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }
        val note = NoteEntity(
            ownerId = ownerId,
            title = title,
            subtitle = body.take(60).ifBlank { "Designed note" },
            body = body,
            category = "Designed",
            imageUri = attachedImage?.toString(),
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao().insert(note)
            }
            Toast.makeText(this@CreateDesignActivity, "Note saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
