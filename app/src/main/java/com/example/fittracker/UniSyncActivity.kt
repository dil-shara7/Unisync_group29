package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Shared base class. After setContentView is called, finds the include_bottom_nav
 * by id and wires the three icons:
 *   nav_left   →  Home (no-op if already on Home)
 *   nav_center →  per-module add; defaults to a "TODO" toast
 *   nav_right  →  Notifications toast (no dedicated screen yet)
 *
 * Activities that don't include the nav are unaffected — findViewById returns null
 * and wiring is skipped.
 */
abstract class UniSyncActivity : AppCompatActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        wireBottomNavIfPresent()
    }

    private fun wireBottomNavIfPresent() {
        val left: View? = findViewById(R.id.nav_left)
        val center: View? = findViewById(R.id.nav_center)
        val right: View? = findViewById(R.id.nav_right)
        if (left == null && center == null && right == null) return

        left?.setOnClickListener {
            if (this !is HomeActivity) {
                startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
        }
        center?.setOnClickListener { onBottomNavCenterClicked() }
        right?.setOnClickListener { onBottomNavRightClicked() }
    }

    /** Module screens override to point [+] at the right "create" flow. */
    protected open fun onBottomNavCenterClicked() {
        Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()
    }

    /** Default 🔔 behavior — no notifications screen yet. */
    protected open fun onBottomNavRightClicked() {
        Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
    }
}
