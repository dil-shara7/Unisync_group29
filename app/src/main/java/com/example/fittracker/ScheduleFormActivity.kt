package com.example.fittracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.ScheduleEventEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityScheduleFormBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ScheduleFormActivity : UniSyncActivity() {

    private lateinit var binding: ActivityScheduleFormBinding
    private var selectedType: String = "Class"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedType = intent.getStringExtra(EXTRA_TYPE) ?: "Class"
        applyChipState()

        binding.backButton.setOnClickListener { finish() }
        binding.chipClass.setOnClickListener { selectedType = "Class"; applyChipState() }
        binding.chipExam.setOnClickListener { selectedType = "Exam"; applyChipState() }
        binding.chipStudy.setOnClickListener { selectedType = "Study Session"; applyChipState() }

        binding.dateField.setOnClickListener { showDatePicker() }
        binding.iconDate.setOnClickListener { showDatePicker() }
        binding.timeField.setOnClickListener { showTimePicker() }
        binding.iconTime.setOnClickListener { showTimePicker() }

        binding.buttonUpdate.setOnClickListener { save() }
    }

    private fun save() {
        val title = binding.inputTitle.text?.toString()?.trim().orEmpty()
        if (title.isEmpty()) {
            binding.inputTitle.error = "Title required"; return
        }
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }
        val event = ScheduleEventEntity(
            ownerId = ownerId,
            title = title,
            type = selectedType,
            date = binding.inputDate.text?.toString().orEmpty(),
            time = binding.inputTime.text?.toString().orEmpty(),
            location = binding.inputLocation.text?.toString().orEmpty(),
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao().insert(event)
            }
            Toast.makeText(this@ScheduleFormActivity, "$selectedType saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun applyChipState() {
        applyChip(binding.chipClass, selectedType == "Class")
        applyChip(binding.chipExam, selectedType == "Exam")
        applyChip(binding.chipStudy, selectedType == "Study Session")
    }

    private fun applyChip(view: TextView, selected: Boolean) {
        view.setBackgroundResource(
            if (selected) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected
        )
        view.setTextColor(
            androidx.core.content.ContextCompat.getColor(
                this,
                if (selected) R.color.white else R.color.text_primary
            )
        )
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            binding.inputDate.setText("%04d-%02d-%02d".format(y, m + 1, d))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, h, min ->
            binding.inputTime.setText("%02d:%02d".format(h, min))
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    override fun onBottomNavCenterClicked() {
        // Already on Schedule create form — no-op
    }

    companion object { const val EXTRA_TYPE = "extra_type" }
}
