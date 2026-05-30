package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import com.example.fittracker.databinding.ActivityLostFoundBinding

class LostFoundActivity : UniSyncActivity() {

    private lateinit var binding: ActivityLostFoundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostFoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        binding.lostReport.setOnClickListener { openReport("lost") }
        binding.lostView.setOnClickListener { openCategories("lost") }
        binding.foundReport.setOnClickListener { openReport("found") }
        binding.foundView.setOnClickListener { openCategories("found") }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, LostFoundReportActivity::class.java))
    }

    private fun openReport(kind: String) {
        startActivity(Intent(this, LostFoundReportActivity::class.java)
            .putExtra(LostFoundReportActivity.EXTRA_KIND, kind))
    }

    private fun openCategories(kind: String) {
        startActivity(Intent(this, LostFoundCategoriesActivity::class.java)
            .putExtra(LostFoundCategoriesActivity.EXTRA_KIND, kind))
    }
}
