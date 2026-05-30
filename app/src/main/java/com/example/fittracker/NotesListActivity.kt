package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityGenericListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesListActivity : UniSyncActivity() {

    private lateinit var binding: ActivityGenericListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenericListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.headerTitle.setText(R.string.notes_my_notes)
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.listRecycler.layoutManager = LinearLayoutManager(this)
        binding.listFab.visibility = View.VISIBLE
        binding.listFab.setOnClickListener {
            startActivity(Intent(this, NotesUploadActivity::class.java))
        }
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
            val items = notes.map { ListEntry(it.title, it.subtitle, imageUri = it.imageUri, id = it.id) }
            binding.listRecycler.adapter = ListEntryAdapter(
                items = items,
                actionLabel = getString(R.string.action_open),
                onClick = {
                    startActivity(Intent(this@NotesListActivity, NoteDetailActivity::class.java)
                        .putExtra(NoteDetailActivity.EXTRA_NOTE_ID, it.id))
                }
            )
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }
}
