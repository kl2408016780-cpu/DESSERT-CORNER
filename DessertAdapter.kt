package com.example.dessertcorner4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.bumptech.glide.Glide

class DessertAdapter(
    private var desserts: List<Dessert>,
    private val onAddToCartClick: (Dessert) -> Unit
) : RecyclerView.Adapter<DessertAdapter.DessertViewHolder>() {

    class DessertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDessert: ShapeableImageView = view.findViewById(R.id.ivDessertImage)
        val tvName: TextView = view.findViewById(R.id.tvDessertName)
        val tvPrice: TextView = view.findViewById(R.id.tvDessertPrice)
        val tvDesc: TextView = view.findViewById(R.id.tvDessertDesc)
        val btnAdd: MaterialButton = view.findViewById(R.id.btnAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DessertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dessert_grid, parent, false)
        return DessertViewHolder(view)
    }

    override fun onBindViewHolder(holder: DessertViewHolder, position: Int) {
        val dessert = desserts[position]
        holder.tvName.text = dessert.name
        holder.tvPrice.text = "RM${String.format("%.2f", dessert.price)}"
        holder.tvDesc.text = dessert.description

        // Image Handling
        val context = holder.itemView.context
        
        if (!dessert.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(dessert.imageUrl)
                .placeholder(R.drawable.ic_dessert_logo)
                .error(R.drawable.ic_dessert_logo)
                .into(holder.ivDessert)
        } else {
            val drawableName = dessert.id?.replace("-", "_") ?: ""
            val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.ivDessert.setImageResource(resourceId)
            } else {
                holder.ivDessert.setImageResource(R.drawable.ic_dessert_logo)
            }
        }

        holder.btnAdd.setOnClickListener {
            onAddToCartClick(dessert)
        }
    }

    override fun getItemCount(): Int = desserts.size

    fun updateData(newDesserts: List<Dessert>) {
        desserts = newDesserts
        notifyDataSetChanged()
    }
}