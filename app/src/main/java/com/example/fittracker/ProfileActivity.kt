package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : UniSyncActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCurrentUser()

        binding.profileBack.setOnClickListener { finish() }
        binding.profileEditButton.setOnClickListener {
            Toast.makeText(this, "Edit profile (TODO)", Toast.LENGTH_SHORT).show()
        }
        binding.rowDashboard.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.rowSettings.setOnClickListener {
            Toast.makeText(this, R.string.profile_settings, Toast.LENGTH_SHORT).show()
        }
        binding.rowNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications (TODO)", Toast.LENGTH_SHORT).show()
        }
        binding.rowLogout.setOnClickListener { logout() }
        binding.tileCart.setOnClickListener {
            startActivity(Intent(this, MarketplaceCartActivity::class.java))
        }
        binding.tileSold.setOnClickListener {
            Toast.makeText(this, "Sold Items (TODO)", Toast.LENGTH_SHORT).show()
        }
        binding.tileWallet.setOnClickListener {
            Toast.makeText(this, "My Wallet (TODO)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCurrentUser() {
        val session = SessionManager(applicationContext)
        val userId = session.currentUserId
        if (userId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).userDao().findById(userId)
            }
            user?.let {
                binding.profileName.text = it.name
                binding.profileEmail.text = it.email
                binding.profileId.text = it.studentId.ifBlank { "Student ID: —" }
                binding.rowStudentId.text = it.studentId.ifBlank { it.name }
                binding.rowFaculty.text = it.faculty.ifBlank { "Computing & Technology" }
                binding.rowProgram.text = it.program.ifBlank { "Bsc (Hons) in Network Science" }
                binding.rowYear.text = it.year.ifBlank { "Year - 3" }
            }
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
    }

    private fun logout() {
        SessionManager(applicationContext).clear()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
