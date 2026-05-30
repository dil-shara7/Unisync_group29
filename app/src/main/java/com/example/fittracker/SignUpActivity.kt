package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.DatabaseSeeder
import com.example.fittracker.data.PasswordHasher
import com.example.fittracker.data.SessionManager
import com.example.fittracker.data.UserEntity
import com.example.fittracker.databinding.ActivitySignupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : UniSyncActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener { register() }
        binding.loginPrompt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun register() {
        val name = binding.nameInput.text?.toString()?.trim().orEmpty()
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()
        val studentId = binding.studentIdInput.text?.toString()?.trim().orEmpty()

        if (name.isEmpty()) { binding.nameInput.error = "Name required"; return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Valid email required"; return
        }
        if (password.length < 6) {
            binding.passwordInput.error = "Min 6 characters"; return
        }

        binding.signupButton.isEnabled = false
        lifecycleScope.launch {
            val db = AppDatabase.get(applicationContext)
            val userDao = db.userDao()

            val existing = withContext(Dispatchers.IO) { userDao.findByEmail(email) }
            if (existing != null) {
                binding.signupButton.isEnabled = true
                Toast.makeText(this@SignUpActivity, "Email already registered", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val hash = withContext(Dispatchers.Default) { PasswordHasher.hash(password) }
            val newUserId = withContext(Dispatchers.IO) {
                userDao.insert(
                    UserEntity(
                        name = name,
                        email = email,
                        passwordHash = hash.hashBase64,
                        passwordSalt = hash.saltBase64,
                        studentId = studentId,
                    )
                )
            }
            // Give the new user a starter set of notes + reminders for demo purposes
            withContext(Dispatchers.IO) { DatabaseSeeder.seedNewUser(db, newUserId) }
            SessionManager(applicationContext).currentUserId = newUserId
            Toast.makeText(this@SignUpActivity, "Welcome, $name!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
            finish()
        }
    }
}
