package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.ScheduleEventEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityScheduleCalendarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Calendar view: month grid + today's events + upcoming events.
 *
 * Events are read from Room. ScheduleFormActivity stores `date` as "yyyy-MM-dd"
 * via DatePicker; legacy/non-conforming strings are skipped from the grouping
 * but kept in the upcoming list as best-effort.
 */
class ScheduleCalendarActivity : UniSyncActivity() {

    private lateinit var binding: ActivityScheduleCalendarBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private var currentMonth: YearMonth = YearMonth.now()
    private val allEvents = mutableListOf<ScheduleEventEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        renderMonthLabels()

        calendarAdapter = CalendarAdapter(
            yearMonth = currentMonth,
            selectedDay = LocalDate.now().dayOfMonth,
        ) { day -> renderSelectedDay(day) }
        binding.calendarGrid.layoutManager = GridLayoutManager(this, 7)
        binding.calendarGrid.adapter = calendarAdapter

        binding.todayEventsRecycler.layoutManager = LinearLayoutManager(this)
        binding.upcomingEventsRecycler.layoutManager = LinearLayoutManager(this)

        binding.monthPrev.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarAdapter.navigateTo(currentMonth)
            renderMonthLabels()
            renderSelectedDay(1)
        }
        binding.monthNext.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarAdapter.navigateTo(currentMonth)
            renderMonthLabels()
            renderSelectedDay(1)
        }
    }

    override fun onResume() {
        super.onResume()
        loadEventsFromRoom()
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, ScheduleFormActivity::class.java))
    }

    private fun loadEventsFromRoom() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            renderSelectedDay(LocalDate.now().dayOfMonth)
            renderUpcoming(emptyList())
            return
        }
        lifecycleScope.launch {
            val events = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao().byOwner(ownerId)
            }
            allEvents.clear()
            allEvents.addAll(events)
            renderSelectedDay(LocalDate.now().dayOfMonth)
            renderUpcoming(events)
        }
    }

    private fun renderMonthLabels() {
        binding.yearLabel.text = currentMonth.year.toString()
        binding.monthLabel.text = currentMonth.format(
            DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        )
    }

    private fun renderSelectedDay(day: Int) {
        val safeDay = day.coerceIn(1, currentMonth.lengthOfMonth())
        val date = LocalDate.of(currentMonth.year, currentMonth.month, safeDay)
        binding.selectedDayBanner.text =
            date.format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.getDefault()))

        val isoDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dayEvents = allEvents
            .filter { it.date == isoDate }
            .map { CalendarEvent(it.title, "${it.time} · ${it.location}".trimSeparators(), it.id) }

        binding.todayEventsRecycler.adapter = CalendarEventAdapter(dayEvents) { event ->
            startActivity(Intent(this, EventDetailActivity::class.java)
                .putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id))
        }
    }

    private fun renderUpcoming(events: List<ScheduleEventEntity>) {
        val today = LocalDate.now()
        val upcoming = events
            .mapNotNull { evt -> parseIsoDate(evt.date)?.let { it to evt } }
            .filter { (date, _) -> !date.isBefore(today) }
            .sortedBy { it.first }
            .take(5)
            .map { (date, evt) ->
                CalendarEvent(
                    evt.title,
                    date.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())),
                    evt.id,
                )
            }
        binding.upcomingEventsRecycler.adapter = CalendarEventAdapter(upcoming) { event ->
            startActivity(Intent(this, EventDetailActivity::class.java)
                .putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id))
        }
    }

    private fun parseIsoDate(value: String): LocalDate? = try {
        LocalDate.parse(value)
    } catch (_: Exception) {
        null
    }

    private fun String.trimSeparators(): String = trim().trim('·', ' ')
}
