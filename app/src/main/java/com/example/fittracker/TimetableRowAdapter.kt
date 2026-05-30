package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimetableRowAdapter(
    private val items: MutableList<String>,
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<TimetableRowAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.row_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_timetable_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val title = items[position]
        holder.title.text = title
        holder.itemView.setOnClickListener { onClick(title) }
    }

    override fun getItemCount(): Int = items.size

    fun filter(query: String, source: List<String>) {
        items.clear()
        items.addAll(
            if (query.isBlank()) source else source.filter { it.contains(query, ignoreCase = true) }
        )
        notifyDataSetChanged()
    }
}
