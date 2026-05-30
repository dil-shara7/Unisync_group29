package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.ScheduleEventEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityViewScheduleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewScheduleActivity : UniSyncActivity() {

    private lateinit var binding: ActivityViewScheduleBinding
    private lateinit var adapter: ScheduleItemAdapter
    private val allEvents = mutableListOf<ScheduleEventEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.headerTitle.text = "View Schedule"
        binding.header.headerBackButton.setOnClickListener { finish() }
        binding.dayHeader.text = "Today's Schedule"

        adapter = ScheduleItemAdapter(mutableListOf()) { title ->
            startActivity(Intent(this, EventDetailActivity::class.java)
                .putExtra(EventDetailActivity.EXTRA_TITLE, title))
        }
        binding.itemsRecycler.layoutManager = LinearLayoutManager(this)
        binding.itemsRecycler.adapter = adapter

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val titles = allEvents.map { it.title }
                adapter.filter(s?.toString().orEmpty(), titles)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val events = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).scheduleEventDao().byOwner(ownerId)
            }
            allEvents.clear()
            allEvents.addAll(events)
            adapter.filter("", events.map { it.title })
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, ScheduleFormActivity::class.java))
    }
}
