package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.fittracker.databinding.ActivityMarketplaceBinding

class MarketplaceActivity : UniSyncActivity() {

    private lateinit var binding: ActivityMarketplaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindCard(binding.catLifestyle, R.string.marketplace_cat_lifestyle, R.drawable.ic_category_lifestyle)
        bindCard(binding.catElectronics, R.string.marketplace_cat_electronics, R.drawable.ic_category_electronics)
        bindCard(binding.catStationery, R.string.marketplace_cat_stationery, R.drawable.ic_category_stationery)
        bindCard(binding.catFurniture, R.string.marketplace_cat_furniture, R.drawable.ic_category_furniture)

        binding.catLifestyle.setOnClickListener { openCategory("Lifestyle on Campus") }
        binding.catElectronics.setOnClickListener { openCategory("Electronics") }
        binding.catStationery.setOnClickListener { openCategory("Stationary & Accessories") }
        binding.catFurniture.setOnClickListener { openCategory("Furniture & Hostel Item") }

        binding.headerSearch.setOnClickListener { showSearchDialog() }
        binding.headerAdd.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, AddItemActivity::class.java))
    }

    private fun bindCard(card: View, labelRes: Int, iconRes: Int) {
        card.findViewById<TextView>(R.id.mp_card_title).setText(labelRes)
        card.findViewById<ImageView>(R.id.mp_card_image).setImageResource(iconRes)
    }

    private fun openCategory(name: String) {
        startActivity(Intent(this, CategoryProductsActivity::class.java)
            .putExtra(CategoryProductsActivity.EXTRA_CATEGORY, name))
    }

    private fun showSearchDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "Search listings"
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Search Marketplace")
            .setView(input)
            .setPositiveButton("Search") { _, _ ->
                val q = input.text.toString().trim()
                if (q.isNotEmpty()) {
                    startActivity(Intent(this, MarketplaceSearchActivity::class.java)
                        .putExtra(MarketplaceSearchActivity.EXTRA_QUERY, q))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
