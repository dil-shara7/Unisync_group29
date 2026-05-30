package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.CartItemEntity
import com.example.fittracker.data.MarketplaceListingEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityMarketplaceSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceSearchActivity : UniSyncActivity() {

    private lateinit var binding: ActivityMarketplaceSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.productsGrid.layoutManager = GridLayoutManager(this, 2)
        binding.headerCart.setOnClickListener {
            startActivity(Intent(this, MarketplaceCartActivity::class.java))
        }

        val initial = intent.getStringExtra(EXTRA_QUERY).orEmpty()
        if (initial.isNotEmpty()) {
            binding.searchInput.setText(initial)
            runSearch(initial)
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                runSearch(s?.toString().orEmpty())
            }
        })
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, AddItemActivity::class.java))
    }

    private fun runSearch(query: String) {
        if (query.isBlank()) {
            binding.productsGrid.adapter = ProductTileAdapter(emptyList(), {}, {})
            binding.emptyState.visibility = View.GONE
            return
        }
        lifecycleScope.launch {
            val listings = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).marketplaceListingDao().search(query)
            }
            val items = listings.map { Product(it.name, "\$${it.price}", it.imageUri) }
            binding.productsGrid.adapter = ProductTileAdapter(
                items = items,
                onOpen = { p ->
                    val listing = listings.firstOrNull { it.name == p.name } ?: return@ProductTileAdapter
                    startActivity(Intent(this@MarketplaceSearchActivity, MarketplaceDetailActivity::class.java)
                        .putExtra(MarketplaceDetailActivity.EXTRA_LISTING_ID, listing.id))
                },
                onAdd = { p ->
                    val listing = listings.firstOrNull { it.name == p.name } ?: return@ProductTileAdapter
                    addToCart(listing)
                }
            )
            binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun addToCart(listing: MarketplaceListingEntity) {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }
        lifecycleScope.launch {
            val dao = AppDatabase.get(applicationContext).cartItemDao()
            val existing = withContext(Dispatchers.IO) { dao.findLine(ownerId, listing.id) }
            withContext(Dispatchers.IO) {
                if (existing != null) {
                    dao.update(existing.copy(quantity = existing.quantity + 1))
                } else {
                    dao.insert(
                        CartItemEntity(
                            ownerId = ownerId,
                            listingId = listing.id,
                            name = listing.name,
                            price = listing.price,
                            quantity = 1,
                            imageUri = listing.imageUri,
                        )
                    )
                }
            }
            Toast.makeText(this@MarketplaceSearchActivity, "${listing.name} added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_QUERY = "extra_query"
    }
}
