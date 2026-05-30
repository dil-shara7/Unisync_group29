package com.example.fittracker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/** Tile shown in the My Notes grid: title + optional image URI. */
data class MyNoteTile(val title: String, val imageUri: String? = null, val id: Long = -1L)

class MyNotesAdapter(
    private val items: List<MyNoteTile>,
    private val onOpen: (MyNoteTile) -> Unit,
) : RecyclerView.Adapter<MyNotesAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.note_label)
        val image: ImageView = view.findViewById(R.id.note_image)
        val open: MaterialButton = view.findViewById(R.id.note_open)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_my_note_tile, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tile = items[position]
        holder.label.text = tile.title
        if (!tile.imageUri.isNullOrEmpty()) {
            try {
                holder.image.setImageURI(Uri.parse(tile.imageUri))
            } catch (_: Exception) {
                holder.image.setImageResource(android.R.drawable.ic_menu_agenda)
            }
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_agenda)
        }
        holder.open.setOnClickListener { onOpen(tile) }
        holder.itemView.setOnClickListener { onOpen(tile) }
    }

    override fun getItemCount(): Int = items.size
}
