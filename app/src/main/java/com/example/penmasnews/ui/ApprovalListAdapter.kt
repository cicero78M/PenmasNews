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
import com.example.penmasnews.model.ChangeLogStorage

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
            item.status = "disetujui"
            notifyItemChanged(position)
            val context = holder.itemView.context
            val logPrefs = context.getSharedPreferences(ChangeLogStorage.PREFS_NAME, android.content.Context.MODE_PRIVATE)
            val logs = ChangeLogStorage.loadLogs(logPrefs)
            val userPrefs = context.getSharedPreferences("user", android.content.Context.MODE_PRIVATE)
            val user = userPrefs.getString("username", "unknown") ?: "unknown"
            logs.add(
                ChangeLogEntry(
                    user,
                    item.status,
                    "workflow approve",
                    System.currentTimeMillis() / 1000L
                )
            )
            ChangeLogStorage.saveLogs(logPrefs, logs)
            onStatusChanged?.invoke(item)
        }

        holder.rejectButton.setOnClickListener {
            item.status = "revisi"
            notifyItemChanged(position)
            val context = holder.itemView.context
            val logPrefs = context.getSharedPreferences(ChangeLogStorage.PREFS_NAME, android.content.Context.MODE_PRIVATE)
            val logs = ChangeLogStorage.loadLogs(logPrefs)
            val userPrefs = context.getSharedPreferences("user", android.content.Context.MODE_PRIVATE)
            val user = userPrefs.getString("username", "unknown") ?: "unknown"
            logs.add(
                ChangeLogEntry(
                    user,
                    item.status,
                    "workflow reject",
                    System.currentTimeMillis() / 1000L
                )
            )
            ChangeLogStorage.saveLogs(logPrefs, logs)
            onStatusChanged?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
