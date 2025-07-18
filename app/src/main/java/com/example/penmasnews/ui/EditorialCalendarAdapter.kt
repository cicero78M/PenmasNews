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
    private val onAiAssist: ((EditorialEvent, Int) -> Unit)? = null,
    private val onDelete: ((EditorialEvent, Int) -> Unit)? = null,
) : RecyclerView.Adapter<EditorialCalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.textDate)
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val notesText: TextView = view.findViewById(R.id.textNotes)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val createdText: TextView = view.findViewById(R.id.textCreated)
        val updatedText: TextView = view.findViewById(R.id.textUpdated)
        val userText: TextView = view.findViewById(R.id.textUser)
        val actionButton: ImageButton = view.findViewById(R.id.buttonAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_editorial_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.dateText.text = "Penjadwalan : ${item.date}"
        holder.titleText.text = "Topik : ${item.topic}"
        holder.notesText.text = "Penugasan: ${item.assignee}"
        holder.statusText.text = "Status : ${item.status}"
        holder.createdText.text = "Created_at : ${DateUtils.formatDateTime(item.createdAt)}"
        holder.updatedText.text = "Last_update : ${DateUtils.formatDateTime(item.lastUpdate)}"
        holder.userText.text = item.username

        holder.itemView.setBackgroundResource(
            if (position % 2 == 0) R.color.zebra_even else R.color.zebra_odd
        )

        holder.actionButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle(R.string.dialog_actions)
                .setItems(arrayOf(
                    holder.itemView.context.getString(R.string.action_open),
                    holder.itemView.context.getString(R.string.action_ai_assist),
                    holder.itemView.context.getString(R.string.action_delete)
                )) { _, which ->
                    when (which) {
                        0 -> onOpen?.invoke(item, position)
                        1 -> onAiAssist?.invoke(item, position)
                        2 -> {
                            val removed = items.removeAt(position)
                            notifyItemRemoved(position)
                            onDelete?.invoke(removed, position)
                        }
                    }
                }
                .show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(event: EditorialEvent) {
        items.add(event)
        notifyItemInserted(items.size - 1)
    }
}
