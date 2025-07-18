package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.ApprovalItem

class ApprovalListAdapter(
    private val items: MutableList<ApprovalItem>,
    private val onAction: ((ApprovalItem, String) -> Unit)? = null,
) : RecyclerView.Adapter<ApprovalListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val requesterText: TextView = view.findViewById(R.id.textRequester)
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
        holder.titleText.text = item.event.topic
        holder.requesterText.text = item.request.requestedBy
        holder.statusText.text = item.request.status

        holder.approveButton.setOnClickListener {
            onAction?.invoke(item, "approved")
        }

        holder.rejectButton.setOnClickListener {
            onAction?.invoke(item, "rejected")
        }
    }

    override fun getItemCount(): Int = items.size
}
