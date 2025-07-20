package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ApprovalItem
import com.example.penmasnews.util.DateUtils

class ApprovalListAdapter(
    private val items: MutableList<ApprovalItem>,
    private val onOpen: ((ApprovalItem) -> Unit)? = null,
) : RecyclerView.Adapter<ApprovalListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.textDate)
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val notesText: TextView = view.findViewById(R.id.textNotes)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val createdByText: TextView = view.findViewById(R.id.textUser)
        val createdText: TextView = view.findViewById(R.id.textCreated)
        val updatedByText: TextView = view.findViewById(R.id.textUpdatedBy)
        val updatedText: TextView = view.findViewById(R.id.textUpdated)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_approval_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val event = item.event
        holder.dateText.text = DateUtils.formatDayDate(event.date)
        holder.titleText.text = event.title
        holder.notesText.text = event.assignee
        holder.statusText.text = item.request.status
        holder.createdByText.text = event.username
        holder.createdText.text = DateUtils.formatDateTime(event.createdAt)
        holder.updatedByText.text = event.updatedBy
        holder.updatedText.text = DateUtils.formatDateTime(event.lastUpdate)

        holder.itemView.setBackgroundResource(
            if (position % 2 == 0) R.color.zebra_even else R.color.zebra_odd
        )

        holder.itemView.setOnClickListener {
            onOpen?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
