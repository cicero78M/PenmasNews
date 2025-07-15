package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent

class EditorialCalendarAdapter(
    private val items: MutableList<EditorialEvent>,
    private val onOpen: ((EditorialEvent) -> Unit)? = null,
    private val onDelete: ((Int) -> Unit)? = null,
) : RecyclerView.Adapter<EditorialCalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.textDate)
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val notesText: TextView = view.findViewById(R.id.textNotes)
        val openButton: Button = view.findViewById(R.id.buttonOpen)
        val deleteButton: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_editorial_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.dateText.text = item.date
        holder.titleText.text = item.topic
        holder.notesText.text = item.assignee
        holder.openButton.setOnClickListener { onOpen?.invoke(item) }
        holder.deleteButton.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
            onDelete?.invoke(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(event: EditorialEvent) {
        items.add(event)
        notifyItemInserted(items.size - 1)
    }
}
