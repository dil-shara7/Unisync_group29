package com.example.fittracker

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittracker.data.AppDatabase
import com.example.fittracker.data.CartItemEntity
import com.example.fittracker.data.SessionManager
import com.example.fittracker.databinding.ActivityShoppingCartBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarketplaceCartActivity : UniSyncActivity() {

    private lateinit var binding: ActivityShoppingCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        adapter = CartAdapter(
            items = mutableListOf(),
            onQuantityChange = { line, newQty -> updateQuantity(line, newQty) },
            onRemove = { line -> removeLine(line) }
        )
        binding.cartRecycler.layoutManager = LinearLayoutManager(this)
        binding.cartRecycler.adapter = adapter

        binding.checkoutButton.setOnClickListener {
            startActivity(Intent(this, MarketplaceCheckoutActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun loadCart() {
        val ownerId = SessionManager(applicationContext).currentUserId
        if (ownerId == SessionManager.NO_USER) {
            binding.emptyState.visibility = android.view.View.VISIBLE
            binding.checkoutButton.isEnabled = false
            return
        }
        lifecycleScope.launch {
            val lines = withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).cartItemDao().byOwner(ownerId)
            }
            adapter.replace(lines)
            refreshTotal(lines)
            binding.emptyState.visibility = if (lines.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            binding.checkoutButton.isEnabled = lines.isNotEmpty()
        }
    }

    private fun updateQuantity(line: CartItemEntity, newQty: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).cartItemDao().update(line.copy(quantity = newQty))
            }
            loadCart()
        }
    }

    private fun removeLine(line: CartItemEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.get(applicationContext).cartItemDao().delete(line)
            }
            loadCart()
        }
    }

    private fun refreshTotal(lines: List<CartItemEntity>) {
        val total = lines.sumOf { it.price * it.quantity }
        binding.totalValue.text = "\$$total"
    }

    override fun onBottomNavCenterClicked() {
        startActivity(Intent(this, AddItemActivity::class.java))
    }
}
