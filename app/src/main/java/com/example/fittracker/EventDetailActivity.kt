package com.example.fittracker

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.ScheduleEventEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityEventDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Loads the named event for the current user, lets the user edit fields and
 * persist via Room, or delete it after confirmation.
 */
class EventDetailActivity : UniSyncActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private var loadedEvent: ScheduleEventEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.buttonEdit.setOnClickListener { enterEditMode() }
        binding.buttonDelete.setOnClickListener { showDeleteDialog() }
        binding.buttonUpdate.setOnClickListener { saveUpdates() }
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        if (id != -1L) {
            loadEventById(id)
        } else {
            val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
            loadEventByTitle(title)
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(android.content.Intent(this, ScheduleFormActivity::class.java))
    }

    private fun loadEventById(id: Long) {
        lifecycleScope.launch {
            val event = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao().byId(id)
            }
            if (event == null) {
                finish(); return@launch
            }
            bind(event)
        }
    }

    private fun loadEventByTitle(title: String) {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val event = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao()
                    .byOwner(ownerId)
                    .firstOrNull { it.title == title }
            }
            if (event != null) bind(event) else {
                Toast.makeText(this@EventDetailActivity, "Event not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun bind(event: ScheduleEventEntity) {
        loadedEvent = event
        binding.eventTitleHeader.text = event.title
        binding.inputTitle.setText(event.title)
        binding.inputDate.setText(event.date)
        binding.inputTime.setText(event.time)
        binding.inputLocation.setText(event.location)
    }

    private fun enterEditMode() {
        binding.inputTitle.isEnabled = true
        binding.inputDate.isEnabled = true
        binding.inputTime.isEnabled = true
        binding.inputLocation.isEnabled = true
        binding.buttonEdit.visibility = View.GONE
        binding.buttonDelete.visibility = View.GONE
        binding.buttonUpdate.visibility = View.VISIBLE
    }

    private fun exitEditMode() {
        binding.inputTitle.isEnabled = false
        binding.inputDate.isEnabled = false
        binding.inputTime.isEnabled = false
        binding.inputLocation.isEnabled = false
        binding.buttonEdit.visibility = View.VISIBLE
        binding.buttonDelete.visibility = View.VISIBLE
        binding.buttonUpdate.visibility = View.GONE
    }

    private fun saveUpdates() {
        val event = loadedEvent ?: return
        val updated = event.copy(
            title = binding.inputTitle.text?.toString().orEmpty(),
            date = binding.inputDate.text?.toString().orEmpty(),
            time = binding.inputTime.text?.toString().orEmpty(),
            location = binding.inputLocation.text?.toString().orEmpty(),
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao().update(updated)
            }
            loadedEvent = updated
            Toast.makeText(this@EventDetailActivity, "Updated", Toast.LENGTH_SHORT).show()
            exitEditMode()
        }
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_delete_confirm)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_delete)
            .setOnClickListener {
                val event = loadedEvent
                if (event != null) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(applicationContext).scheduleEventDao().delete(event)
                        }
                        Toast.makeText(this@EventDetailActivity, "${event.title} deleted", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        finish()
                    }
                } else {
                    dialog.dismiss()
                    finish()
                }
            }
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_EVENT_ID = "extra_event_id"
    }
}
