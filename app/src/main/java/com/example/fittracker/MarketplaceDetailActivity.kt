package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.MarketplaceListingEntity
import com.example.fittracker.databinding.ActivityProductDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceDetailActivity : UniSyncActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var loaded: MarketplaceListingEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.buttonEdit.setOnClickListener { openEdit() }

        val listingId = intent.getLongExtra(EXTRA_LISTING_ID, -1L)
        if (listingId == -1L) {
            val name = intent.getStringExtra(EXTRA_TITLE) ?: "MacBook Air M1 (2020)"
            val price = intent.getStringExtra(EXTRA_SUBTITLE) ?: "\$650"
            renderFallback(name, price)
        }
    }

    override fun onResume() {
        super.onResume()
        val listingId = intent.getLongExtra(EXTRA_LISTING_ID, -1L)
        if (listingId != -1L) loadListing(listingId)
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, AddItemActivity::class.java))
    }

    private fun loadListing(id: Long) {
        lifecycleScope.launch {
            val listing = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).marketplaceListingDao().byId(id)
            }
            loaded = listing
            if (listing != null) {
                render(listing)
            } else {
                // Listing was deleted while we were away — close detail page
                finish()
            }
        }
    }

    private fun render(listing: MarketplaceListingEntity) {
        binding.productName.text = listing.name
        binding.productPrice.text = "\$${listing.price}"
        binding.productDescription.text = listing.description.ifBlank {
            "Lightly used. In good condition."
        }
        binding.productCondition.text = "Condition: ${listing.condition.ifBlank { "Used – Like New" }}"
        binding.productSeller.text = "Seller - ${listing.sellerName.ifBlank { "John Axy" }}"
        binding.productContact.text = "Contact - ${listing.contact.ifBlank { "+94 77 123 4567" }}"
        if (!listing.imageUri.isNullOrBlank()) {
            try {
                binding.productImage.setImageURI(android.net.Uri.parse(listing.imageUri))
            } catch (_: Exception) {
                binding.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            binding.productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun renderFallback(name: String, price: String) {
        binding.productName.text = name
        binding.productPrice.text = price
        binding.productDescription.text = "Lightly used. In good condition."
        binding.productCondition.text = "Condition: Used – Like New"
        binding.productSeller.text = "Seller - John Axy"
        binding.productContact.text = "Contact - +94 77 123 4567"
    }

    private fun openEdit() {
        val listing = loaded
        val intent = Intent(this, EditItemActivity::class.java)
        if (listing != null) {
            intent.putExtra(EditItemActivity.EXTRA_LISTING_ID, listing.id)
            intent.putExtra(EditItemActivity.EXTRA_NAME, listing.name)
            intent.putExtra(EditItemActivity.EXTRA_DESCRIPTION, listing.description)
            intent.putExtra(EditItemActivity.EXTRA_CONTACT, listing.contact)
        } else {
            intent.putExtra(EditItemActivity.EXTRA_NAME, binding.productName.text.toString())
            intent.putExtra(EditItemActivity.EXTRA_DESCRIPTION, binding.productDescription.text.toString())
            intent.putExtra(EditItemActivity.EXTRA_CONTACT, "+94 77 123 4567")
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_LISTING_ID = "extra_listing_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SUBTITLE = "extra_subtitle"
    }
}
