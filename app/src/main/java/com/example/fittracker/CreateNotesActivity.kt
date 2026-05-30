package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityCreateNotesBinding

/**
 * Create Notes — Figma "Study Note Hub 3 / 4".
 * New Note collapsible (designs grid) + More / Show less toggle + Recent + Favorite headers.
 */
class CreateNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityCreateNotesBinding
    private var moreShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "Create Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }

        binding.toggleMore.setOnClickListener { toggleMore() }

        // Any design tile → open editor with that design as starting template
        listOf(
            binding.designBlank,
            binding.design1,
            binding.design2,
            binding.design2b,
            binding.design3,
            binding.designNew,
        ).forEach { tile ->
            tile.setOnClickListener {
                startActivity(Intent(this, CreateDesignActivity::class.java))
            }
        }

        binding.sectionRecent.setOnClickListener {
            startActivity(Intent(this, RecentNotesActivity::class.java))
        }
        binding.sectionFavorite.setOnClickListener {
            startActivity(Intent(this, FavoriteNotesActivity::class.java))
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }

    private fun toggleMore() {
        moreShown = !moreShown
        binding.designsRow2.visibility = if (moreShown) View.VISIBLE else View.GONE
        binding.toggleMore.text = if (moreShown) "‹ Show less" else "More Designs ›"
    }
}
