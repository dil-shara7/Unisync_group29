package com.example.fittracker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class EditNoteTile(val title: String, val imageUri: String? = null, val id: Long = -1L)

class EditNotesAdapter(
    private val items: MutableList<EditNoteTile>,
    private val onEdit: (EditNoteTile) -> Unit,
    private val onDelete: (EditNoteTile) -> Unit,
) : RecyclerView.Adapter<EditNotesAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tile_label)
        val image: ImageView = view.findViewById(R.id.tile_image)
        val edit: ImageView = view.findViewById(R.id.tile_edit)
        val delete: ImageView = view.findViewById(R.id.tile_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_edit_note_tile, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tile = items[position]
        holder.label.text = tile.title
        if (!tile.imageUri.isNullOrEmpty()) {
            try {
                holder.image.setImageURI(Uri.parse(tile.imageUri))
            } catch (_: Exception) {
                holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        holder.edit.setOnClickListener { onEdit(tile) }
        holder.delete.setOnClickListener { onDelete(tile) }
    }

    override fun getItemCount(): Int = items.size

    fun remove(tile: EditNoteTile) {
        val index = items.indexOf(tile)
        if (index >= 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
