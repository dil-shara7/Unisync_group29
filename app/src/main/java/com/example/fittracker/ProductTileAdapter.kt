package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ProductTileAdapter(
    private val items: List<Product>,
    private val onOpen: (Product) -> Unit,
    private val onAdd: (Product) -> Unit,
) : RecyclerView.Adapter<ProductTileAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.product_name)
        val price: TextView = view.findViewById(R.id.product_price)
        val viewDetails: MaterialButton = view.findViewById(R.id.product_view_details)
        val add: ImageView = view.findViewById(R.id.product_add)
        val image: ImageView = view.findViewById(R.id.product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_product_tile, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.name.text = p.name
        holder.price.text = p.price
        if (!p.imageUri.isNullOrEmpty()) {
            try {
                holder.image.setImageURI(android.net.Uri.parse(p.imageUri))
            } catch (_: Exception) {
                holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        holder.viewDetails.setOnClickListener { onOpen(p) }
        holder.image.setOnClickListener { onOpen(p) }
        holder.add.setOnClickListener { onAdd(p) }
    }

    override fun getItemCount(): Int = items.size
}
