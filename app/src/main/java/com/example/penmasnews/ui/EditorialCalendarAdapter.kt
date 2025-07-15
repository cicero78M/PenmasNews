package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent

class EditorialCalendarAdapter(
    private val items: List<EditorialEvent>
) : RecyclerView.Adapter<EditorialCalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.textDate)
        val topicText: TextView = view.findViewById(R.id.textTopic)
        val assigneeText: TextView = view.findViewById(R.id.textAssignee)
        val statusText: TextView = view.findViewById(R.id.textStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_editorial_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.dateText.text = item.date
        holder.topicText.text = item.topic
        holder.assigneeText.text = item.assignee
        holder.statusText.text = item.status
    }

    override fun getItemCount(): Int = items.size
}
