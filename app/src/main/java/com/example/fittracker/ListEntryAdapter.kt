package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ListEntryAdapter(
    private val items: List<ListEntry>,
    private val onClick: (ListEntry) -> Unit,
    private val actionLabel: String = "View Details",
) : RecyclerView.Adapter<ListEntryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.list_item_image)
        val title: TextView = view.findViewById(R.id.list_item_title)
        val subtitle: TextView = view.findViewById(R.id.list_item_subtitle)
        val action: MaterialButton = view.findViewById(R.id.list_item_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_list_item, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        if (!item.imageUri.isNullOrEmpty()) {
            try {
                holder.image.setImageURI(android.net.Uri.parse(item.imageUri))
            } catch (_: Exception) {
                holder.image.setImageResource(item.iconRes)
            }
        } else {
            holder.image.setImageResource(item.iconRes)
        }
        holder.action.text = actionLabel
        holder.itemView.setOnClickListener { onClick(item) }
        holder.action.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
