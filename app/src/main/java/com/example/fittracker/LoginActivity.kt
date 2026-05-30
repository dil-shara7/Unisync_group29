package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.PasswordHasher
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : UniSyncActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        styleSignUpPrompt()
        wireClickHandlers()
    }

    private fun wireClickHandlers() {
        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.forgotPasswordText.setOnClickListener { onForgotPassword() }
        binding.signupPrompt.setOnClickListener { onSignUpClicked() }
        binding.googleButton.setOnClickListener { onGoogleClicked() }
        binding.facebookButton.setOnClickListener { onFacebookClicked() }
    }

    private fun attemptLogin() {
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Valid email required"
            return
        }
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password required"
            return
        }

        binding.loginButton.isEnabled = false
        lifecycleScope.launch {
            val userDao = AppDatabase.get(applicationContext).userDao()
            val user = withContext(Dispatchers.IO) { userDao.findByEmail(email) }
            if (user == null) {
                binding.loginButton.isEnabled = true
                binding.emailInput.error = "No account with that email"
                return@launch
            }
            val ok = withContext(Dispatchers.Default) {
                PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)
            }
            if (!ok) {
                binding.loginButton.isEnabled = true
                binding.passwordInput.error = "Wrong password"
                return@launch
            }
            SessionManager(applicationContext).currentUserId = user.id
            Toast.makeText(this@LoginActivity, "Welcome back, ${user.name}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun onForgotPassword() {
        Toast.makeText(this, "Forgot password (TODO)", Toast.LENGTH_SHORT).show()
    }

    private fun onSignUpClicked() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun onGoogleClicked() {
        Toast.makeText(this, "Google sign-in (TODO)", Toast.LENGTH_SHORT).show()
    }

    private fun onFacebookClicked() {
        Toast.makeText(this, "Facebook sign-in (TODO)", Toast.LENGTH_SHORT).show()
    }

    private fun styleSignUpPrompt() {
        val fullText = getString(R.string.auth_no_account)
        val linkWord = getString(R.string.auth_sign_up)
        val start = fullText.lastIndexOf(linkWord)
        if (start < 0) return
        val spannable = SpannableString(fullText)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.text_link)),
            start,
            start + linkWord.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.signupPrompt.text = spannable
    }
}
