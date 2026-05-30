package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityNotesHubBinding

class NotesHubActivity : UniSyncActivity() {

    private lateinit var binding: ActivityNotesHubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.setText(R.string.notes_title)
        binding.header.headerBackButton.setOnClickListener { finish() }

        binding.cardManage.setOnClickListener {
            startActivity(Intent(this, ManageNotesActivity::class.java))
        }
        binding.cardView.setOnClickListener {
            startActivity(Intent(this, ViewNotesActivity::class.java))
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }
}
