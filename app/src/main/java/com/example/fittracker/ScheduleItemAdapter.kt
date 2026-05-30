package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Simple row adapter: title text + chevron. Used on View Schedule and similar
 * single-column schedule lists. Tap → invokes onClick with the title string.
 */
class ScheduleItemAdapter(
    private val items: MutableList<String>,
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<ScheduleItemAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_schedule_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val title = items[position]
        holder.title.text = title
        holder.itemView.setOnClickListener { onClick(title) }
    }

    override fun getItemCount(): Int = items.size

    fun remove(title: String) {
        val index = items.indexOf(title)
        if (index >= 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun filter(query: String, source: List<String>) {
        items.clear()
        if (query.isBlank()) {
            items.addAll(source)
        } else {
            items.addAll(source.filter { it.contains(query, ignoreCase = true) })
        }
        notifyDataSetChanged()
    }
}
