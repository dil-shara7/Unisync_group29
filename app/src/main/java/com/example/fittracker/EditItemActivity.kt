package com.example.fittracker

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.MarketplaceListingEntity
import com.example.fittracker.databinding.ActivityEditItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditItemActivity : UniSyncActivity() {

    private lateinit var binding: ActivityEditItemBinding
    private var loaded: MarketplaceListingEntity? = null
    private var newImage: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* not persistable */ }
            newImage = uri
            binding.productImage.setImageURI(uri)
            Toast.makeText(this, "Image changed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputName.setText(intent.getStringExtra(EXTRA_NAME) ?: "MacBook Air M1 (2020)")
        binding.inputDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION)
            ?: "8GB RAM, 256GB SSD. Battery health is excellent.")
        binding.inputContact.setText(intent.getStringExtra(EXTRA_CONTACT) ?: "+94 77 123 4567")

        val id = intent.getLongExtra(EXTRA_LISTING_ID, -1L)
        if (id != -1L) loadListing(id)

        binding.backButton.setOnClickListener { finish() }
        binding.headerDelete.setOnClickListener { showDeleteDialog() }
        binding.buttonChangeImage.setOnClickListener { pickImage.launch(arrayOf("image/*")) }
        binding.buttonUpdate.setOnClickListener { saveUpdates() }
        binding.buttonCancel.setOnClickListener { finish() }
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
            listing?.imageUri?.let { uriString ->
                try {
                    binding.productImage.setImageURI(Uri.parse(uriString))
                } catch (_: Exception) { /* placeholder */ }
            }
        }
    }

    private fun saveUpdates() {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) { binding.inputName.error = "Name required"; return }
        val existing = loaded
        if (existing == null) {
            Toast.makeText(this, "Saved (no record bound)", Toast.LENGTH_SHORT).show()
            finish(); return
        }
        val updated = existing.copy(
            name = name,
            description = binding.inputDescription.text?.toString().orEmpty(),
            contact = binding.inputContact.text?.toString().orEmpty(),
            imageUri = newImage?.toString() ?: existing.imageUri,
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).marketplaceListingDao().update(updated)
            }
            Toast.makeText(this@EditItemActivity, "Item updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_message_alert)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.findViewById<TextView>(R.id.dialog_body).text =
            "Do you want to delete this item permanently?"
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_delete)
            .setOnClickListener {
                val existing = loaded
                if (existing != null) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.get(applicationContext).marketplaceListingDao().delete(existing)
                        }
                        Toast.makeText(this@EditItemActivity, "Item deleted", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        finish()
                    }
                } else {
                    dialog.dismiss(); finish()
                }
            }
        dialog.findViewById<com.google.android.material.button.MaterialButton>(R.id.dialog_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object {
        const val EXTRA_LISTING_ID = "extra_listing_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_CONTACT = "extra_contact"
    }
}
