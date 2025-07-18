package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.model.ChangeLogDatabase

class ApprovalListAdapter(
    private val items: MutableList<EditorialEvent>,
    private val onStatusChanged: ((EditorialEvent) -> Unit)? = null,
) : RecyclerView.Adapter<ApprovalListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val approveButton: Button = view.findViewById(R.id.buttonApprove)
        val rejectButton: Button = view.findViewById(R.id.buttonReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_approval_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.topic
        holder.statusText.text = item.status

        holder.approveButton.setOnClickListener {
            item.status = "approved"
            notifyItemChanged(position)
            val context = holder.itemView.context
            val authPrefs = context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
            val user = authPrefs.getString("username", "unknown") ?: "unknown"
            ChangeLogDatabase.addLog(
                context,
                ChangeLogEntry(
                    user,
                    item.status,
                    "workflow approve",
                    System.currentTimeMillis() / 1000L
                )
            )
            onStatusChanged?.invoke(item)
        }

        holder.rejectButton.setOnClickListener {
            item.status = "rejected"
            notifyItemChanged(position)
            val context = holder.itemView.context
            val authPrefs = context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
            val user = authPrefs.getString("username", "unknown") ?: "unknown"
            ChangeLogDatabase.addLog(
                context,
                ChangeLogEntry(
                    user,
                    item.status,
                    "workflow reject",
                    System.currentTimeMillis() / 1000L
                )
            )
            onStatusChanged?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
