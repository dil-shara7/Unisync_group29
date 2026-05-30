package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityCollapsibleNotesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityCollapsibleNotesBinding
    private var recentExpanded: Boolean = true
    private var favoriteExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollapsibleNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "Recent Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.recentRecycler.layoutManager = LinearLayoutManager(this)
        binding.favoriteRecycler.layoutManager = LinearLayoutManager(this)

        binding.sectionNew.setOnClickListener {
            binding.chevronNew.rotation = if (binding.chevronNew.rotation == 270f) 90f else 270f
        }
        binding.sectionRecent.setOnClickListener {
            recentExpanded = !recentExpanded; applySectionState()
        }
        binding.sectionFavorite.setOnClickListener {
            favoriteExpanded = !favoriteExpanded; applySectionState()
        }
    }

    override fun onResume() {
        super.onResume()
        loadContent()
    }

    private fun loadContent() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val dao = AppDatabase.get(applicationContext).noteDao()
            val recent = withContext(Dispatchers.IO) { dao.byOwner(ownerId).take(6) }
            val favorites = withContext(Dispatchers.IO) { dao.favoritesByOwner(ownerId) }

            bindRecycler(binding.recentRecycler, recent.map { ListEntry(it.title, it.subtitle, imageUri = it.imageUri, id = it.id) })
            bindRecycler(binding.favoriteRecycler, favorites.map { ListEntry(it.title, it.subtitle, imageUri = it.imageUri, id = it.id) })
            applySectionState()
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }

    private fun bindRecycler(recycler: RecyclerView, items: List<ListEntry>) {
        recycler.adapter = ListEntryAdapter(
            items = items,
            actionLabel = getString(R.string.action_open),
            onClick = {
                startActivity(Intent(this, NoteDetailActivity::class.java)
                    .putExtra(NoteDetailActivity.EXTRA_NOTE_ID, it.id))
            }
        )
    }

    private fun applySectionState() {
        binding.chevronRecent.rotation = if (recentExpanded) 90f else 270f
        binding.chevronFavorite.rotation = if (favoriteExpanded) 90f else 270f
        val recentHas = (binding.recentRecycler.adapter?.itemCount ?: 0) > 0
        val favHas = (binding.favoriteRecycler.adapter?.itemCount ?: 0) > 0
        binding.recentRecycler.visibility = if (recentExpanded && recentHas) View.VISIBLE else View.GONE
        binding.emptyRecent.visibility = if (recentExpanded && !recentHas) View.VISIBLE else View.GONE
        binding.favoriteRecycler.visibility = if (favoriteExpanded && favHas) View.VISIBLE else View.GONE
        binding.emptyFavorite.visibility = if (favoriteExpanded && !favHas) View.VISIBLE else View.GONE
    }
}
