package com.example.dessertcorner4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.bumptech.glide.Glide

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (String, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivItem: ShapeableImageView = view.findViewById(R.id.ivCartItem)
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val tvQty: TextView = view.findViewById(R.id.tvCartItemQty)
        val btnIncrease: ImageButton = view.findViewById(R.id.btnIncreaseQty)
        val btnDecrease: ImageButton = view.findViewById(R.id.btnDecreaseQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        val dessert = item.dessert

        holder.tvName.text = dessert?.name ?: "Unknown"
        holder.tvPrice.text = "RM${String.format("%.2f", dessert?.price ?: 0.0)}"
        holder.tvQty.text = item.quantity.toString()

        // Image Handling
        val context = holder.itemView.context
        
        if (!dessert?.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(dessert?.imageUrl)
                .placeholder(R.drawable.ic_dessert_logo)
                .error(R.drawable.ic_dessert_logo)
                .into(holder.ivItem)
        } else {
            val drawableName = dessert?.id?.replace("-", "_") ?: ""
            val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.ivItem.setImageResource(resourceId)
            } else {
                holder.ivItem.setImageResource(R.drawable.ic_dessert_logo)
            }
        }

        holder.btnIncrease.setOnClickListener {
            onQuantityChanged(dessert?.id ?: "", item.quantity + 1)
        }

        holder.btnDecrease.setOnClickListener {
            onQuantityChanged(dessert?.id ?: "", item.quantity - 1)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
}
