package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityManageScheduleBinding

class ManageScheduleActivity : UniSyncActivity() {

    private lateinit var binding: ActivityManageScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "Manage Schedule"
        binding.header.headerBackButton.setOnClickListener { finish() }

        binding.cardCreate.setOnClickListener {
            startActivity(Intent(this, CreateScheduleActivity::class.java))
        }
        binding.cardView.setOnClickListener {
            startActivity(Intent(this, ViewScheduleActivity::class.java))
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, ScheduleFormActivity::class.java))
    }
}
