package com.example.dessertcorner4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdminDessertAdapter(
    private val context: Context,
    private val dessertList: List<Dessert>
) : RecyclerView.Adapter<AdminDessertAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onEditClick(dessert: Dessert)
        fun onDeleteClick(dessert: Dessert)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDessert: ImageView = view.findViewById(R.id.ivAdminDessertImage)
        val tvName: TextView = view.findViewById(R.id.tvAdminDessertName)
        val tvPrice: TextView = view.findViewById(R.id.tvAdminDessertPrice)
        val tvStock: TextView = view.findViewById(R.id.tvAdminStock)
        val ivEdit: ImageView = view.findViewById(R.id.ivEditDessert)
        val ivDelete: ImageView = view.findViewById(R.id.ivDeleteDessert)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_admin_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dessert = dessertList[position]
        holder.tvName.text = dessert.name ?: "Unknown"
        holder.tvPrice.text = "RM ${String.format("%.2f", dessert.price)}"
        holder.tvStock.text = "Stock: ${dessert.stock}"

        if (!dessert.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(dessert.imageUrl)
                .placeholder(R.drawable.ic_dessert_logo)
                .into(holder.ivDessert)
        } else {
            holder.ivDessert.setImageResource(R.drawable.ic_dessert_logo)
        }

        // Setting up listeners for specific icons
        holder.ivEdit.setOnClickListener {
            listener?.onEditClick(dessert)
        }

        holder.ivDelete.setOnClickListener {
            listener?.onDeleteClick(dessert)
        }
        
        // Also allow clicking the whole item to Edit (for convenience)
        holder.itemView.setOnClickListener {
            listener?.onEditClick(dessert)
        }
    }

    override fun getItemCount(): Int = dessertList.size
}
