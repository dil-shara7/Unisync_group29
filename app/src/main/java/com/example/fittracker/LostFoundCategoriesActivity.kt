package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.databinding.ActivityLfCategoriesBinding

class LostFoundCategoriesActivity : UniSyncActivity() {

    private lateinit var binding: ActivityLfCategoriesBinding
    private lateinit var adapter: LFCategoryAdapter
    private var kind: String = "lost"

    private val allCategories: List<LFCategory>
        get() = listOf(
            LFCategory("Stationary & Accessories", R.drawable.ic_lf_cat_stationery),
            LFCategory("Electronics", R.drawable.ic_lf_cat_electronics),
            LFCategory("Wallets", R.drawable.ic_lf_cat_wallets),
            LFCategory("Keys", R.drawable.ic_lf_cat_keys),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLfCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kind = intent.getStringExtra(EXTRA_KIND) ?: "lost"
        binding.headerTitle.text = if (kind == "found") "Found Items" else "Lost Items"
        binding.backButton.setOnClickListener { finish() }

        adapter = LFCategoryAdapter(allCategories.toMutableList()) { category -> openCategory(category) }
        binding.categoriesRecycler.layoutManager = LinearLayoutManager(this)
        binding.categoriesRecycler.adapter = adapter

        binding.othersButton.setOnClickListener { openCategory("Others") }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString().orEmpty().trim()
                val filtered = if (q.isEmpty()) allCategories
                else allCategories.filter { it.name.contains(q, ignoreCase = true) }
                adapter.replaceWith(filtered)
            }
        })

        binding.filterButton.setOnClickListener { showSortDialog() }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, LostFoundReportActivity::class.java)
            .putExtra(LostFoundReportActivity.EXTRA_KIND, kind))
    }

    private fun openCategory(category: String) {
        startActivity(Intent(this, LostFoundItemsActivity::class.java)
            .putExtra(LostFoundItemsActivity.EXTRA_CATEGORY, category)
            .putExtra(LostFoundItemsActivity.EXTRA_KIND, kind))
    }

    private fun showSortDialog() {
        val options = arrayOf("A → Z", "Z → A")
        AlertDialog.Builder(this)
            .setTitle("Sort categories")
            .setItems(options) { _, which ->
                val sorted = when (which) {
                    0 -> allCategories.sortedBy { it.name }
                    1 -> allCategories.sortedByDescending { it.name }
                    else -> allCategories
                }
                adapter.replaceWith(sorted)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object { const val EXTRA_KIND = "extra_kind" }
}
