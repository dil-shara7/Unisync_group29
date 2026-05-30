package com.example.fittracker

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.LostFoundItemEntity
import com.example.fittracker.databinding.ActivityLfItemsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LostFoundItemsActivity : UniSyncActivity() {

    private lateinit var binding: ActivityLfItemsBinding
    private lateinit var adapter: LFItemAdapter
    private val source = mutableListOf<LostFoundItemEntity>()
    private var category: String = "Stationary & Accessories"
    private var kind: String = "lost"
    private var activeLocationFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLfItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Stationary & Accessories"
        kind = intent.getStringExtra(EXTRA_KIND) ?: "lost"
        binding.headerTitle.text = category
        binding.backButton.setOnClickListener { finish() }

        adapter = LFItemAdapter(mutableListOf()) { item -> showDeleteDialog(item) }
        adapter.setKind(kind)
        binding.itemsRecycler.layoutManager = LinearLayoutManager(this)
        binding.itemsRecycler.adapter = adapter

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }
        })

        binding.filterButton.setOnClickListener { showFilterDialog() }

        loadItems()
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, LostFoundReportActivity::class.java)
            .putExtra(LostFoundReportActivity.EXTRA_KIND, kind))
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    private fun loadItems() {
        lifecycleScope.launch {
            val rows = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).lostFoundDao()
                    .byStatusAndCategory(kind, category)
            }
            source.clear()
            source.addAll(rows)
            applyFilters()
        }
    }

    private fun applyFilters() {
        val q = binding.searchInput.text?.toString()?.trim().orEmpty()
        val loc = activeLocationFilter
        val filtered = source.filter { row ->
            (q.isEmpty() || row.name.contains(q, ignoreCase = true)) &&
                (loc.isNullOrEmpty() || row.location.contains(loc, ignoreCase = true))
        }
        adapter.replaceWith(filtered.map { toUiItem(it) })
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun toUiItem(row: LostFoundItemEntity): LostFoundItem = LostFoundItem(
        name = row.name,
        meta = if (row.timePeriod.isBlank() && row.location.isBlank()) ""
               else "${row.timePeriod} at ${row.location}".trim(),
        foundBy = row.foundBy.ifBlank { "Sakuni Anupama" },
        date = "02/01/2026",
        contact = row.contact.ifBlank { "071-1589645" },
        imageUri = row.imageUri,
    )

    private fun showFilterDialog() {
        val locations = source.map { it.location }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
        if (locations.isEmpty()) {
            Toast.makeText(this, "No locations to filter by", Toast.LENGTH_SHORT).show()
            return
        }
        val choices = (listOf("All locations") + locations).toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Filter by location")
            .setItems(choices) { _, which ->
                activeLocationFilter = if (which == 0) null else choices[which]
                applyFilters()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog(item: LostFoundItem) {
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
                val row = source.firstOrNull { it.name == item.name }
                if (row != null) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(applicationContext).lostFoundDao().delete(row)
                        }
                        source.remove(row)
                        applyFilters()
                        Toast.makeText(this@LostFoundItemsActivity, "${item.name} deleted", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } else dialog.dismiss()
            }
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_KIND = "extra_kind"
    }
}
