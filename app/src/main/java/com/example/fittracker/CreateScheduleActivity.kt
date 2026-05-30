package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivityCreateScheduleBinding

class CreateScheduleActivity : UniSyncActivity() {

    private lateinit var binding: ActivityCreateScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.typeClass.setOnClickListener { openForm("Class") }
        binding.typeExam.setOnClickListener { openForm("Exam") }
        binding.typeStudy.setOnClickListener { openForm("Study Session") }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, ScheduleFormActivity::class.java))
    }

    private fun openForm(type: String) {
        startActivity(Intent(this, ScheduleFormActivity::class.java)
            .putExtra(ScheduleFormActivity.EXTRA_TYPE, type))
    }
}
