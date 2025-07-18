package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ChangeLogEntry
import com.example.penmasnews.util.DateUtils

class LogListAdapter(private val items: List<ChangeLogEntry>) :
    RecyclerView.Adapter<LogListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.textLog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_entry, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        val formatted = DateUtils.formatTimestamp(entry.timestamp)
        holder.text.text = "$formatted - ${entry.user} - ${entry.status} - ${entry.changes}"
    }
}
