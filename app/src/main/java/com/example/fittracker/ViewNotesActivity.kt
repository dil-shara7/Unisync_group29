package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityViewNotesBinding

class ViewNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityViewNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "View Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }

        binding.cardMyNotes.setOnClickListener {
            startActivity(Intent(this, MyNotesActivity::class.java))
        }
        binding.cardSavedNotes.setOnClickListener {
            startActivity(Intent(this, SavedNotesActivity::class.java))
        }
        binding.cardInternet.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=study+notes"))
            startActivity(intent)
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }
}
