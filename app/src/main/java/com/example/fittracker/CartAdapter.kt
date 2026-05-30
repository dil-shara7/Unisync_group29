package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fittracker.data.CartItemEntity

class CartAdapter(
    private val items: MutableList<CartItemEntity>,
    private val onQuantityChange: (CartItemEntity, Int) -> Unit,
    private val onRemove: (CartItemEntity) -> Unit,
) : RecyclerView.Adapter<CartAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cart_image)
        val name: TextView = view.findViewById(R.id.cart_name)
        val qty: TextView = view.findViewById(R.id.cart_qty)
        val price: TextView = view.findViewById(R.id.cart_price)
        val minus: TextView = view.findViewById(R.id.cart_minus)
        val plus: TextView = view.findViewById(R.id.cart_plus)
        val delete: ImageView = view.findViewById(R.id.cart_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_cart_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val line = items[position]
        holder.name.text = line.name
        holder.qty.text = line.quantity.toString()
        holder.price.text = "\$${line.price * line.quantity}"

        if (!line.imageUri.isNullOrBlank()) {
            try {
                holder.image.setImageURI(android.net.Uri.parse(line.imageUri))
                holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
            } catch (_: Exception) {
                holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.minus.setOnClickListener {
            if (line.quantity > 1) onQuantityChange(line, line.quantity - 1)
        }
        holder.plus.setOnClickListener { onQuantityChange(line, line.quantity + 1) }
        holder.delete.setOnClickListener { onRemove(line) }
    }

    override fun getItemCount(): Int = items.size

    fun replace(newItems: List<CartItemEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
