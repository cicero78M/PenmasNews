package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent

/**
 * Adapter for displaying approved events ready to publish.
 */
class CmsIntegrationAdapter(
    private val items: List<EditorialEvent>,
    private val onPublish: (EditorialEvent) -> Unit,
) : RecyclerView.Adapter<CmsIntegrationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.textDate)
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val notesText: TextView = view.findViewById(R.id.textNotes)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val actionButton: ImageButton = view.findViewById(R.id.buttonAction)
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
        holder.statusText.text = item.status
        holder.itemView.setBackgroundResource(
            if (position % 2 == 0) R.color.zebra_even else R.color.zebra_odd
        )
        holder.actionButton.setOnClickListener {
            onPublish(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
