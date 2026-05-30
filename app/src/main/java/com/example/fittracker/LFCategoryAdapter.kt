package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LFCategory(val name: String, val iconRes: Int)

class LFCategoryAdapter(
    private val items: MutableList<LFCategory>,
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<LFCategoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.category_label)
        val icon: ImageView = view.findViewById(R.id.category_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_lf_category_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cat = items[position]
        holder.label.text = cat.name
        holder.icon.setImageResource(cat.iconRes)
        holder.itemView.setOnClickListener { onClick(cat.name) }
    }

    override fun getItemCount(): Int = items.size

    fun replaceWith(newItems: List<LFCategory>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
