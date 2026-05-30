package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityCheckoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceCheckoutActivity : UniSyncActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.editDelivery.setOnClickListener { binding.deliveryAddress.requestFocus() }

        val radios = listOf(
            binding.paymentCardRadio,
            binding.paymentPaypalRadio,
            binding.paymentCodRadio,
        )
        val rows = listOf(
            binding.paymentCard to binding.paymentCardRadio,
            binding.paymentPaypal to binding.paymentPaypalRadio,
            binding.paymentCod to binding.paymentCodRadio,
        )
        rows.forEach { (row, radio) ->
            row.setOnClickListener {
                radios.forEach { it.isChecked = false }
                radio.isChecked = true
            }
        }

        loadSummary()

        binding.confirmButton.setOnClickListener { confirm() }
    }

    private fun loadSummary() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) return
        lifecycleScope.launch {
            val lines = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).cartItemDao().byOwner(ownerId)
            }
            val subTotal = lines.sumOf { it.price * it.quantity }
            val shipping = 10
            val service = 5
            val total = subTotal + shipping + service
            binding.subTotal.text = "\$$subTotal"
            binding.totalValue.text = "\$$total"
        }
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, AddItemActivity::class.java))
    }

    private fun confirm() {
        val ownerId = SessionManager(applicationContext).currentUserId
        lifecycleScope.launch {
            if (ownerId != SessionManager.NO_USER) {
                withContext(Dispatchers.IO) {
                    AppDatabase.get(applicationContext).cartItemDao().clearCart(ownerId)
                }
            }
            Toast.makeText(this@MarketplaceCheckoutActivity, "Payment confirmed", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MarketplaceCheckoutActivity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
    }
}
