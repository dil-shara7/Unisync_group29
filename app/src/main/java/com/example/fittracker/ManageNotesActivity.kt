package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityManageNotesBinding

class ManageNotesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityManageNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "Manage Notes"
        binding.header.headerBackButton.setOnClickListener { finish() }

        binding.cardCreate.setOnClickListener {
            startActivity(Intent(this, CreateNotesActivity::class.java))
        }
        binding.cardUpload.setOnClickListener {
            startActivity(Intent(this, NotesUploadActivity::class.java))
        }
        binding.cardEdit.setOnClickListener {
            startActivity(Intent(this, EditNotesActivity::class.java))
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, NotesUploadActivity::class.java))
    }
}
