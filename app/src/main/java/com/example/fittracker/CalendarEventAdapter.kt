package com.example.fittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CalendarEvent(val title: String, val meta: String, val id: Long = -1L)

class CalendarEventAdapter(
    private val items: List<CalendarEvent>,
    private val onClick: (CalendarEvent) -> Unit,
) : RecyclerView.Adapter<CalendarEventAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.event_row_title)
        val meta: TextView = view.findViewById(R.id.event_row_meta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_calendar_event_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val event = items[position]
        holder.title.text = event.title
        holder.meta.text = event.meta
        holder.itemView.setOnClickListener { onClick(event) }
    }

    override fun getItemCount(): Int = items.size
}
