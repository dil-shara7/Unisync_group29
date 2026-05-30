package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.fittracker.databinding.ActivitySplashBinding

class SplashActivity : UniSyncActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashRunnable = Runnable { goToLogin() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handler.postDelayed(splashRunnable, SPLASH_DURATION_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacks(splashRunnable)
        super.onDestroy()
    }

    private fun goToLogin() {
        val session = com.example.fittracker.data.SessionManager(applicationContext)
        val next = if (session.isLoggedIn) HomeActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }

    private companion object {
        const val SPLASH_DURATION_MS = 1800L
    }
}
