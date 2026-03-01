package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView

class BrowseSubspaceAdapter(
    private val subspaceList: List<Subspace>,
    private val showRequestButton: Boolean,
    private val onRequestClick: (Subspace) -> Unit,
    private val onItemClick: (Subspace) -> Unit

) : RecyclerView.Adapter<BrowseSubspaceAdapter.ViewHolder>()
 {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSubspaceName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvSubspaceDescription)
        val tvInviteCode: TextView = itemView.findViewById(R.id.tvInviteCode)
        val btnAction: MaterialButton = itemView.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subspace, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = subspaceList.size

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         val subspace = subspaceList[position]

         holder.tvName.text = subspace.name
         holder.tvDescription.text = subspace.description
         holder.tvInviteCode.text = "Invite Code: ${subspace.inviteCode}"

         holder.itemView.setOnClickListener {
             onItemClick(subspace)
         }


         if (showRequestButton) {
             holder.btnAction.visibility = View.VISIBLE
             holder.btnAction.text = "Request Admin Access"

             holder.btnAction.setOnClickListener {
                 onRequestClick(subspace)
             }
         } else {
             holder.btnAction.visibility = View.GONE
         }
     }


 }
