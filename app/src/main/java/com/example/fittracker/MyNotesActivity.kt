package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityMyNotesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityMyNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.headerTitle.text = "My Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.notesGrid.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val tiles = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao()
                    .byOwner(ownerId)
                    .map { MyNoteTile(it.title, it.imageUri, it.id) }
            }
            binding.notesGrid.adapter = MyNotesAdapter(tiles) { tile ->
                startActivity(Intent(this@MyNotesActivity, NoteDetailActivity::class.java)
                    .putExtra(NoteDetailActivity.EXTRA_NOTE_ID, tile.id))
            }
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }
}
