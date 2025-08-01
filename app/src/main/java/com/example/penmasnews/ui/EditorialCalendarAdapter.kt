package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.util.DateUtils

class EditorialCalendarAdapter(
    private val items: MutableList<EditorialEvent>,
    private val onOpen: ((EditorialEvent, Int) -> Unit)? = null,
    private val onViewLogs: ((EditorialEvent, Int) -> Unit)? = null,
    private val onAiAssist: ((EditorialEvent, Int) -> Unit)? = null,
    private val onDelete: ((EditorialEvent, Int) -> Unit)? = null,
    private val onPublish: ((EditorialEvent, Int) -> Unit)? = null,
    private val onPublishWordpress: ((EditorialEvent, Int) -> Unit)? = null,
) : RecyclerView.Adapter<EditorialCalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.textDate)
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val newsTitleText: TextView = view.findViewById(R.id.textNewsTitle)
        val notesText: TextView = view.findViewById(R.id.textNotes)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val createdByText: TextView = view.findViewById(R.id.textUser)
        val createdText: TextView = view.findViewById(R.id.textCreated)
        val updatedByText: TextView = view.findViewById(R.id.textUpdatedBy)
        val updatedText: TextView = view.findViewById(R.id.textUpdated)
        val actionButton: ImageButton = view.findViewById(R.id.buttonAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_editorial_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.dateText.text = "Penjadwalan : ${DateUtils.formatDayDate(item.date)}"
        holder.titleText.text = "Topik : ${item.topic}"
        holder.newsTitleText.text = "Judul : ${item.title}"
        holder.notesText.text = "Penugasan : ${item.assignee}"
        holder.statusText.text = "Status: ${item.status}"
        holder.createdByText.text = "Created by : ${item.username}"
        holder.createdText.text = "Created at : ${DateUtils.formatDateTime(item.createdAt)}"
        holder.updatedByText.text = "Updated by : ${item.updatedBy}"
        holder.updatedText.text = "Last Updated : ${DateUtils.formatDateTime(item.lastUpdate)}"

        holder.itemView.setBackgroundResource(
            if (position % 2 == 0) R.color.zebra_even else R.color.zebra_odd
        )

        holder.actionButton.setOnClickListener {
            if (item.status == "approved") {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle(R.string.dialog_actions)
                    .setItems(
                        arrayOf(
                            holder.itemView.context.getString(R.string.action_publish_blogspot),
                            holder.itemView.context.getString(R.string.action_publish_wordpress)
                        )
                    ) { _, which ->
                        when (which) {
                            0 -> onPublish?.invoke(item, position)
                            1 -> onPublishWordpress?.invoke(item, position)
                        }
                    }
                    .show()
            } else {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle(R.string.dialog_actions)
                    .setItems(arrayOf(
                        holder.itemView.context.getString(R.string.action_open),
                        holder.itemView.context.getString(R.string.action_view_logs),
                        holder.itemView.context.getString(R.string.action_ai_assist),
                        holder.itemView.context.getString(R.string.action_delete)
                    )) { _, which ->
                        when (which) {
                            0 -> onOpen?.invoke(item, position)
                            1 -> onViewLogs?.invoke(item, position)
                            2 -> onAiAssist?.invoke(item, position)
                            3 -> {
                                val removed = items.removeAt(position)
                                notifyItemRemoved(position)
                                onDelete?.invoke(removed, position)
                            }
                        }
                    }
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(event: EditorialEvent) {
        items.add(event)
        notifyItemInserted(items.size - 1)
    }
}
