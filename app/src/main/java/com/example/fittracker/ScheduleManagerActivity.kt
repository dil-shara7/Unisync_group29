package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.fittracker.databinding.ActivityScheduleManagerBinding

/**
 * Landing for the Schedule module. 4 cards:
 *   - Manage Schedule → ManageScheduleActivity (Create / View flow)
 *   - Timetable       → TimetableListActivity
 *   - Calendar        → ScheduleCalendarActivity
 *   - Reminder        → ScheduleReminderActivity
 *
 * Bottom-nav [+] short-cut: goes straight to ScheduleFormActivity so users can
 * add a new event from any schedule screen.
 */
class ScheduleManagerActivity : UniSyncActivity() {

    private lateinit var binding: ActivityScheduleManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.setText(R.string.schedule_title)
        binding.header.headerBackButton.setOnClickListener { finish() }

        bindCard(binding.cardManage, R.string.schedule_manage, R.drawable.ic_module_manage_schedule)
        bindCard(binding.cardTimetable, R.string.schedule_timetable, R.drawable.ic_module_timetable)
        bindCard(binding.cardCalendar, R.string.schedule_calendar, R.drawable.ic_module_calendar)
        bindCard(binding.cardReminder, R.string.schedule_reminder, R.drawable.ic_module_reminder)

        binding.cardManage.setOnClickListener { go(ManageScheduleActivity::class.java) }
        binding.cardTimetable.setOnClickListener { go(TimetableListActivity::class.java) }
        binding.cardCalendar.setOnClickListener { go(ScheduleCalendarActivity::class.java) }
        binding.cardReminder.setOnClickListener { go(ScheduleReminderActivity::class.java) }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, ScheduleFormActivity::class.java))
    }

    private fun bindCard(card: View, labelRes: Int, iconRes: Int) {
        card.findViewById<TextView>(R.id.module_card_label).setText(labelRes)
        card.findViewById<ImageView>(R.id.module_card_icon).setImageResource(iconRes)
    }

    private fun go(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }
}
