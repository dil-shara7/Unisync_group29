package com.example.fittracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 7 (days) × 6 (slots) grid. Each cell is either empty or shows a class label
 * coloured by its ColorTag (CLASS / EXAM / STUDY).
 *
 * Adapter is fed a Map<Pair<slot,day>, TimetableCell> so lookup is O(1).
 */
class TimetableAdapter(cells: List<TimetableCell>) :
    RecyclerView.Adapter<TimetableAdapter.VH>() {

    private val cellByIndex: Map<Int, TimetableCell> =
        cells.associateBy { it.slot * 7 + it.day }

    class VH(view: TextView) : RecyclerView.ViewHolder(view) {
        val label: TextView = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_timetable_cell, parent, false) as TextView
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cell = cellByIndex[position]
        if (cell == null) {
            holder.label.text = ""
            holder.label.setBackgroundResource(R.drawable.bg_timetable_empty)
        } else {
            holder.label.text = cell.label
            holder.label.setBackgroundResource(when (cell.color) {
                ColorTag.CLASS -> R.drawable.bg_timetable_class
                ColorTag.EXAM -> R.drawable.bg_timetable_exam
                ColorTag.STUDY -> R.drawable.bg_timetable_study
            })
        }
    }

    override fun getItemCount(): Int = 42  // 7 days × 6 slots
}
