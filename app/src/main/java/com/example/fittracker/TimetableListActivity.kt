package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityTimetableListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimetableListActivity : UniSyncActivity() {

    private lateinit var binding: ActivityTimetableListBinding
    private lateinit var adapter: TimetableRowAdapter
    private val allTitles = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "Time Table"
        binding.header.headerBackButton.setOnClickListener { finish() }

        adapter = TimetableRowAdapter(mutableListOf()) { title ->
            startActivity(Intent(this, EventDetailActivity::class.java)
                .putExtra(EventDetailActivity.EXTRA_TITLE, title))
        }
        binding.timetableRecycler.layoutManager = LinearLayoutManager(this)
        binding.timetableRecycler.adapter = adapter

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s?.toString().orEmpty(), allTitles)
            }
        })

        binding.calendarButton.setOnClickListener {
            startActivity(Intent(this, ScheduleCalendarActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val titles = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao().byOwner(ownerId).map { it.title }
            }
            allTitles.clear()
            allTitles.addAll(titles)
            adapter.filter("", titles)
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, ScheduleFormActivity::class.java))
    }
}
