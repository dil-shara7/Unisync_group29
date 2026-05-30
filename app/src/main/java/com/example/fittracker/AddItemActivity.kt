package com.example.fittracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.MarketplaceListingEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityAddItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddItemActivity : UniSyncActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private var pickedImage: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* not persistable */ }
            pickedImage = uri
            binding.imagePreview.visibility = android.view.View.VISIBLE
            binding.imagePreview.setImageURI(uri)
            binding.imagePlaceholder.visibility = android.view.View.GONE
            Toast.makeText(this, "Image attached", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.imagePicker.setOnClickListener { pickImage.launch(arrayOf("image/*")) }
        binding.buttonDone.setOnClickListener { save() }

        binding.inputCategory.isFocusable = false
        binding.inputCategory.isClickable = true
        binding.inputCategory.setOnClickListener { showCategoryPicker() }
    }

    private fun showCategoryPicker() {
        val categories = arrayOf(
            "Lifestyle on Campus",
            "Electronics",
            "Stationary & Accessories",
            "Furniture & Hostel Item",
        )
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select category")
            .setItems(categories) { _, which ->
                binding.inputCategory.setText(categories[which])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onBottomNavCenterClicked() {
        // Already on Add Item — no-op
    }

    private fun save() {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        val priceStr = binding.inputPrice.text?.toString()?.trim().orEmpty()
        val category = binding.inputCategory.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) { binding.inputName.error = "Name required"; return }
        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show(); return
        }
        if (priceStr.isEmpty()) { binding.inputPrice.error = "Price required"; return }
        val price = priceStr.removePrefix("\$").toIntOrNull()
        if (price == null) { binding.inputPrice.error = "Numeric price required"; return }

        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }

        val listing = MarketplaceListingEntity(
            ownerId = ownerId,
            name = name,
            category = category,
            price = price,
            description = binding.inputDescription.text?.toString().orEmpty(),
            imageUri = pickedImage?.toString(),
            sellerName = "You",
            contact = "—",
            condition = "Used – Like New",
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).marketplaceListingDao().insert(listing)
            }
            Toast.makeText(this@AddItemActivity, "Item posted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
