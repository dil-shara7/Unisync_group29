package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivitySavedNotesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivitySavedNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "Saved Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.savedRecycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadSaved()
    }

    private fun loadSaved() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val notes = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).noteDao().savedByOwner(ownerId)
            }
            if (notes.isEmpty()) {
                binding.savedRecycler.visibility = View.GONE
                binding.emptyPanel.visibility = View.VISIBLE
            } else {
                binding.emptyPanel.visibility = View.GONE
                binding.savedRecycler.visibility = View.VISIBLE
                binding.savedRecycler.adapter = ListEntryAdapter(
                    items = notes.map { ListEntry(it.title, it.subtitle, imageUri = it.imageUri, id = it.id) },
                    actionLabel = getString(R.string.action_open),
                    onClick = {
                        startActivity(Intent(this@SavedNotesActivity, NoteDetailActivity::class.java)
                            .putExtra(NoteDetailActivity.EXTRA_NOTE_ID, it.id))
                    }
                )
            }
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }
}
