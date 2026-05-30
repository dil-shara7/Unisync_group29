package com.example.fittracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.LostFoundItemEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityLfReportBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LostFoundReportActivity : UniSyncActivity() {

    private lateinit var binding: ActivityLfReportBinding
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
        binding = ActivityLfReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getStringExtra(EXTRA_KIND)) {
            "lost" -> binding.statusLost.isChecked = true
            "found" -> binding.statusFound.isChecked = true
        }

        binding.backButton.setOnClickListener { finish() }
        binding.imagePicker.setOnClickListener { pickImage.launch(arrayOf("image/*")) }
        binding.buttonUpdate.setOnClickListener { save() }

        binding.inputCategory.isFocusable = false
        binding.inputCategory.isClickable = true
        binding.inputCategory.setOnClickListener { showCategoryPicker() }
    }

    override fun onBottomNavCenterClicked() {
        // Already on Report — no-op
    }

    private fun showCategoryPicker() {
        val categories = arrayOf("Stationary & Accessories", "Electronics", "Wallets", "Keys", "Others")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select category")
            .setItems(categories) { _, which ->
                binding.inputCategory.setText(categories[which])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun save() {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputName.error = "Item name required"; return
        }
        val status = when {
            binding.statusLost.isChecked -> "lost"
            binding.statusFound.isChecked -> "found"
            else -> {
                Toast.makeText(this, "Select status", Toast.LENGTH_SHORT).show(); return
            }
        }
        val reporterId = SessionManager(applicationContext).currentUserId
        if (reporterId == SessionManager.NO_USER) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show(); return
        }
        val item = LostFoundItemEntity(
            reporterId = reporterId,
            name = name,
            category = binding.inputCategory.text?.toString()?.trim().orEmpty().ifBlank { "Others" },
            location = binding.inputLocation.text?.toString().orEmpty(),
            timePeriod = binding.inputTime.text?.toString().orEmpty(),
            status = status,
            imageUri = pickedImage?.toString(),
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).lostFoundDao().insert(item)
            }
            Toast.makeText(this@LostFoundReportActivity, "Report submitted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object { const val EXTRA_KIND = "extra_kind" }
}
