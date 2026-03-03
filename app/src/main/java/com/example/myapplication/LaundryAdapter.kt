package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaundryAdapter : RecyclerView.Adapter<LaundryAdapter.ViewHolder>() {

    private var list = listOf<LaundryModel>()

    fun submitList(newList: List<LaundryModel>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvRegNo: TextView = view.findViewById(R.id.tvRegNo)
        val tvClothes: TextView = view.findViewById(R.id.tvClothes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laundry, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.name
        holder.tvRegNo.text = "Reg: ${item.regNo}"
        holder.tvClothes.text = "Clothes: ${item.clothesCount}"
    }
}