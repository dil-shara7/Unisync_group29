package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.CartItemEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityCategoryProductsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryProductsActivity : UniSyncActivity() {

    private lateinit var binding: ActivityCategoryProductsBinding
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Lifestyle on Campus"
        binding.headerTitle.text = displayName(category)
        binding.backButton.setOnClickListener { finish() }

        binding.productsGrid.layoutManager = GridLayoutManager(this, 2)

        binding.headerSearch.setOnClickListener {
            startActivity(Intent(this, MarketplaceSearchActivity::class.java))
        }
        binding.headerCart.setOnClickListener {
            startActivity(Intent(this, MarketplaceCartActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts(category)
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, AddItemActivity::class.java))
    }

    private fun loadProducts(category: String) {
        lifecycleScope.launch {
            val listings = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).marketplaceListingDao().byCategory(category)
            }
            val items = listings.map { Product(it.name, "\$${it.price}", it.imageUri) }
            binding.productsGrid.adapter = ProductTileAdapter(
                items = items,
                onOpen = { p ->
                    val listing = listings.firstOrNull { it.name == p.name } ?: return@ProductTileAdapter
                    startActivity(Intent(this@CategoryProductsActivity, MarketplaceDetailActivity::class.java)
                        .putExtra(MarketplaceDetailActivity.EXTRA_LISTING_ID, listing.id))
                },
                onAdd = { p ->
                    val listing = listings.firstOrNull { it.name == p.name } ?: return@ProductTileAdapter
                    addToCart(listing.id, listing.name, listing.price, listing.imageUri)
                }
            )
        }
    }

    private fun addToCart(listingId: Long, name: String, price: Int, imageUri: String?) {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }
        lifecycleScope.launch {
            val dao = AppDatabase.get(applicationContext).cartItemDao()
            val existing = withContext(Dispatchers.IO) { dao.findLine(ownerId, listingId) }
            withContext(Dispatchers.IO) {
                if (existing != null) {
                    dao.update(existing.copy(quantity = existing.quantity + 1))
                } else {
                    dao.insert(
                        CartItemEntity(
                            ownerId = ownerId,
                            listingId = listingId,
                            name = name,
                            price = price,
                            quantity = 1,
                            imageUri = imageUri,
                        )
                    )
                }
            }
            Toast.makeText(this@CategoryProductsActivity, "$name added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayName(category: String): String = when (category) {
        "Lifestyle on Campus" -> "Life Style on Campus"
        else -> category
    }

    companion object { const val EXTRA_CATEGORY = "extra_category" }
}
