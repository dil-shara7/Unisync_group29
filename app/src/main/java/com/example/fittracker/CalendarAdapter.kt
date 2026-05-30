package com.example.fittracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.YearMonth

/**
 * Month-grid: leading blanks pad the first row so day 1 lands on the correct
 * weekday column. Selected day rendered in deep purple, others in white tiles.
 */
class CalendarAdapter(
    private var yearMonth: YearMonth,
    private var selectedDay: Int,
    private val onDayClick: (Int) -> Unit,
) : RecyclerView.Adapter<CalendarAdapter.VH>() {

    private var daysInMonth: Int = yearMonth.lengthOfMonth()
    private var leadingBlanks: Int = (yearMonth.atDay(1).dayOfWeek.value + 6) % 7
    private var totalCells: Int = ((leadingBlanks + daysInMonth + 6) / 7) * 7

    class VH(val cell: TextView) : RecyclerView.ViewHolder(cell)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_calendar_day, parent, false) as TextView
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val dayNumber = position - leadingBlanks + 1
        if (dayNumber < 1 || dayNumber > daysInMonth) {
            holder.cell.text = ""
            holder.cell.background = null
            holder.cell.setOnClickListener(null)
            return
        }
        holder.cell.text = dayNumber.toString()
        val isSelected = dayNumber == selectedDay
        holder.cell.setBackgroundResource(
            if (isSelected) R.drawable.bg_calendar_day_selected
            else R.drawable.bg_calendar_day_default
        )
        holder.cell.setTextColor(
            androidx.core.content.ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.white else R.color.text_primary
            )
        )
        holder.cell.setOnClickListener {
            val previousSelected = selectedDay
            selectedDay = dayNumber
            notifyItemChanged(leadingBlanks + previousSelected - 1)
            notifyItemChanged(position)
            onDayClick(dayNumber)
        }
    }

    override fun getItemCount(): Int = totalCells

    fun navigateTo(newYearMonth: YearMonth) {
        yearMonth = newYearMonth
        selectedDay = 1
        daysInMonth = yearMonth.lengthOfMonth()
        leadingBlanks = (yearMonth.atDay(1).dayOfWeek.value + 6) % 7
        totalCells = ((leadingBlanks + daysInMonth + 6) / 7) * 7
        notifyDataSetChanged()
    }
}
