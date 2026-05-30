package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Expandable Lost & Found item row. "see more" reveals reporter info + delete icon
 * (Lost & Found - 10). Delete prompts the host activity via [onDeleteRequest].
 */
class LFItemAdapter(
    private val items: MutableList<LostFoundItem>,
    private val onDeleteRequest: (LostFoundItem) -> Unit,
) : RecyclerView.Adapter<LFItemAdapter.VH>() {

    private val expanded = mutableSetOf<Int>()
    private var kind: String = "lost"

    fun setKind(k: String) { kind = k }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.row_image)
        val name: TextView = view.findViewById(R.id.row_name)
        val meta: TextView = view.findViewById(R.id.row_meta)
        val seeMore: TextView = view.findViewById(R.id.row_see_more)
        val divider: View = view.findViewById(R.id.row_divider)
        val detail: View = view.findViewById(R.id.row_detail)
        val foundBy: TextView = view.findViewById(R.id.detail_found_by)
        val date: TextView = view.findViewById(R.id.detail_date)
        val phone: TextView = view.findViewById(R.id.detail_contact_phone)
        val delete: ImageView = view.findViewById(R.id.detail_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_lf_item_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.meta.text = item.meta
        val label = if (kind == "found") "Found by" else "Reported by"
        holder.foundBy.text = "$label: ${item.foundBy}"
        holder.date.text = "Date : ${item.date}"
        holder.phone.text = item.contact
        if (!item.imageUri.isNullOrEmpty()) {
            try {
                holder.image.setImageURI(android.net.Uri.parse(item.imageUri))
            } catch (_: Exception) {
                holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        val isOpen = position in expanded
        holder.divider.visibility = if (isOpen) View.VISIBLE else View.GONE
        holder.detail.visibility = if (isOpen) View.VISIBLE else View.GONE
        holder.seeMore.text = if (isOpen) "see less" else "see more"

        holder.seeMore.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            if (pos in expanded) expanded.remove(pos) else expanded.add(pos)
            notifyItemChanged(pos)
        }
        holder.delete.setOnClickListener { onDeleteRequest(item) }
    }

    override fun getItemCount(): Int = items.size

    fun remove(item: LostFoundItem) {
        val index = items.indexOf(item)
        if (index >= 0) {
            items.removeAt(index)
            expanded.clear()
            notifyDataSetChanged()
        }
    }

    fun replaceWith(newItems: List<LostFoundItem>) {
        items.clear()
        items.addAll(newItems)
        expanded.clear()
        notifyDataSetChanged()
    }
}
