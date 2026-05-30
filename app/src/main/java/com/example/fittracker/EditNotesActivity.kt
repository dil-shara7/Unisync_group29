package com.example.fittracker

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityEditNotesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityEditNotesBinding
    private lateinit var adapter: EditNotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.headerTitle.text = "Edit Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.editNotesGrid.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val notes = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao().byOwner(ownerId)
            }
            val tiles = notes.map { EditNoteTile(it.title, it.imageUri, it.id) }
            adapter = EditNotesAdapter(
                tiles.toMutableList(),
                onEdit = { tile ->
                    startActivity(Intent(this@EditNotesActivity, NotesUploadActivity::class.java)
                        .putExtra(NotesUploadActivity.EXTRA_NOTE_ID, tile.id))
                },
                onDelete = { showDeleteDialog(it) }
            )
            binding.editNotesGrid.adapter = adapter
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }

    private fun showDeleteDialog(tile: EditNoteTile) {
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
                lifecycleScope.launch {
                    val note = withContext(Dispatchers.IO) {
                        AppDatabase.get(applicationContext).noteDao().byId(tile.id)
                    }
                    if (note != null) {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(applicationContext).noteDao().delete(note)
                        }
                        if (::adapter.isInitialized) adapter.remove(tile)
                        Toast.makeText(this@EditNotesActivity, "${tile.title} deleted", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
