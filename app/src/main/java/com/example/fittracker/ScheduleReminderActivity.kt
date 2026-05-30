package com.example.fittracker

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.ReminderEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityGenericListBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Reminders list. Backed by Room (ReminderDao). FAB or bottom-nav [+] opens
 * the Add Reminder dialog; tapping a row opens the Reminder pop-up; the
 * row's "Open" action triggers a delete-confirm dialog.
 */
class ScheduleReminderActivity : UniSyncActivity() {

    private lateinit var binding: ActivityGenericListBinding
    private var currentReminders: List<ReminderEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenericListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.headerTitle.setText(R.string.schedule_reminder)
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.listRecycler.layoutManager = LinearLayoutManager(this)
        binding.listFab.visibility = View.VISIBLE
        binding.listFab.setOnClickListener { showAddDialog() }
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    override fun onBottomNavCenterClicked() {
        showAddDialog()
    }

    private fun loadReminders() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            currentReminders = emptyList()
            renderList()
            return
        }
        lifecycleScope.launch {
            val reminders = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).reminderDao().byOwner(ownerId)
            }
            currentReminders = reminders
            renderList()
        }
    }

    private fun renderList() {
        val items = currentReminders.map { ListEntry(it.title, it.dueAt) }
        binding.listRecycler.adapter = ListEntryAdapter(
            items = items,
            actionLabel = getString(R.string.action_delete),
            onClick = { entry ->
                val reminder = currentReminders.firstOrNull { it.title == entry.title }
                if (reminder != null) showDeleteDialog(reminder)
                else showReminderPopup(entry.title, entry.subtitle)
            }
        )
    }

    private fun showAddDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_reminder)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val titleField = dialog.findViewById<EditText>(R.id.reminder_input_title)
        val dueField = dialog.findViewById<EditText>(R.id.reminder_input_due)
        dialog.findViewById<MaterialButton>(R.id.reminder_dialog_add).setOnClickListener {
            val title = titleField.text?.toString()?.trim().orEmpty()
            val due = dueField.text?.toString()?.trim().orEmpty()
            if (title.isEmpty()) {
                titleField.error = "Title required"
                return@setOnClickListener
            }
            val ownerId = SessionManager(applicationContext).currentUserId
            if (ownerId == SessionManager.NO_USER) {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
                dialog.dismiss(); return@setOnClickListener
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AppDatabase.get(applicationContext).reminderDao()
                        .insert(ReminderEntity(ownerId = ownerId, title = title, dueAt = due))
                }
                Toast.makeText(this@ScheduleReminderActivity, "Reminder added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadReminders()
            }
        }
        dialog.findViewById<MaterialButton>(R.id.reminder_dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showDeleteDialog(reminder: ReminderEntity) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_delete_confirm)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.findViewById<MaterialButton>(R.id.dialog_delete).setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AppDatabase.get(applicationContext).reminderDao().delete(reminder)
                }
                Toast.makeText(this@ScheduleReminderActivity, "${reminder.title} deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadReminders()
            }
        }
        dialog.findViewById<MaterialButton>(R.id.dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showReminderPopup(classLine: String, roomLine: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_reminder)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.findViewById<TextView>(R.id.reminder_class).text = classLine
        dialog.findViewById<TextView>(R.id.reminder_room).text = roomLine
        dialog.findViewById<MaterialButton>(R.id.dialog_ok)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
