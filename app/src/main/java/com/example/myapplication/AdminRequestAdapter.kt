package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AdminRequestAdapter(
    private val requestList: List<AdminRequest>,
    private val onApprove: (AdminRequest) -> Unit,
    private val onReject: (AdminRequest) -> Unit
) : RecyclerView.Adapter<AdminRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        val btnApprove: MaterialButton = itemView.findViewById(R.id.btnApprove)
        val btnReject: MaterialButton = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = requestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val request = requestList[position]

        holder.tvUserId.text = "User: ${request.userId}"

        holder.btnApprove.setOnClickListener {
            onApprove(request)
        }

        holder.btnReject.setOnClickListener {
            onReject(request)
        }
    }
}
