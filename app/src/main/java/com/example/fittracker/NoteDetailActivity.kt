package com.example.fittracker

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.NoteEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityNoteDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteDetailActivity : UniSyncActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private var loadedNote: NoteEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.backTitle.setOnClickListener { finish() }

        binding.actionShare.setOnClickListener {
            val title = loadedNote?.title ?: binding.noteTitle.text.toString()
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out my note: $title")
            }
            startActivity(Intent.createChooser(share, "Share via"))
        }
        binding.actionDownload.setOnClickListener {
            Toast.makeText(this, "Successfully Downloaded", Toast.LENGTH_SHORT).show()
        }
        binding.actionBookmark.setOnClickListener { toggleSaved() }
        binding.actionDelete.setOnClickListener { showDeleteDialog() }
        binding.actionEdit.setOnClickListener { openEdit() }
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (id != -1L) {
            loadNoteById(id)
        } else {
            val title = intent.getStringExtra(EXTRA_TITLE) ?: "Note"
            binding.noteTitle.text = title
            loadNoteByTitle(title)
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }

    private fun openEdit() {
        val note = loadedNote
        if (note == null) {
            // If not yet loaded, retry shortly
            binding.root.postDelayed({ openEdit() }, 200); return
        }
        startActivity(Intent(this, NotesUploadActivity::class.java)
            .putExtra(NotesUploadActivity.EXTRA_NOTE_ID, note.id))
        finish()
    }

    private fun loadNoteById(id: Long) {
        lifecycleScope.launch {
            val note = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao().byId(id)
            }
            if (note == null) {
                Toast.makeText(this@NoteDetailActivity, "Note not found", Toast.LENGTH_SHORT).show()
                finish(); return@launch
            }
            bind(note)
        }
    }

    private fun loadNoteByTitle(title: String) {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val note = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao()
                    .byOwner(ownerId)
                    .firstOrNull { it.title == title }
            }
            if (note == null) {
                Toast.makeText(this@NoteDetailActivity, "Note not found", Toast.LENGTH_SHORT).show()
                finish(); return@launch
            }
            bind(note)
        }
    }

    private fun bind(note: NoteEntity) {
        loadedNote = note
        binding.noteTitle.text = note.title
        applyBookmarkIcon()
        if (!note.imageUri.isNullOrBlank()) {
            try {
                binding.noteImage.setImageURI(android.net.Uri.parse(note.imageUri))
            } catch (_: Exception) {
                binding.noteImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            binding.noteImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        binding.noteBody.text = note.body.ifBlank { note.subtitle }

        // Auto-open edit if requested by caller (EditNotes pencil)
        if (intent.getBooleanExtra(EXTRA_EDIT_MODE, false)) {
            intent.removeExtra(EXTRA_EDIT_MODE)
            binding.root.post { openEdit() }
        }
    }

    private fun toggleSaved() {
        val note = loadedNote ?: return
        val updated = note.copy(isSaved = !note.isSaved)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao().update(updated)
            }
            loadedNote = updated
            applyBookmarkIcon()
            Toast.makeText(
                this@NoteDetailActivity,
                if (updated.isSaved) "Added to Saved Notes" else "Removed from Saved Notes",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun applyBookmarkIcon() {
        binding.actionBookmark.setImageResource(
            if (loadedNote?.isSaved == true) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_message_alert)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.findViewById<TextView>(R.id.dialog_body).text =
            "Are you sure you want to delete this file?"
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_delete)
            .setOnClickListener {
                val note = loadedNote
                if (note != null) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(applicationContext).noteDao().delete(note)
                        }
                        Toast.makeText(this@NoteDetailActivity, "${note.title} deleted", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        finish()
                    }
                } else {
                    dialog.dismiss()
                    finish()
                }
            }
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SUBTITLE = "extra_subtitle"
        const val EXTRA_EDIT_MODE = "extra_edit_mode"
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
