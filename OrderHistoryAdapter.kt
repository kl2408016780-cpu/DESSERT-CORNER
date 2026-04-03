package com.example.dessertcorner4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvHistoryOrderId)
        val tvDate: TextView = view.findViewById(R.id.tvHistoryDate)
        val tvItems: TextView = view.findViewById(R.id.tvHistoryItems)
        val tvTotal: TextView = view.findViewById(R.id.tvHistoryTotal)
        val tvStatus: TextView = view.findViewById(R.id.tvHistoryStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "#${order.id}"
        
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(order.timestamp))
        
        val itemsString = order.items.joinToString(", ") { "${it.dessert?.name} x${it.quantity}" }
        holder.tvItems.text = itemsString
        holder.tvTotal.text = "RM ${String.format("%.2f", order.total)}"
        
        holder.tvStatus.text = order.status.uppercase()
        val colorRes = when(order.status) {
            "Preparing" -> R.color.status_preparing
            "Out for Delivery" -> R.color.status_out_for_delivery
            "Delivered" -> R.color.status_delivered
            "Cancelled" -> R.color.status_cancelled
            else -> R.color.status_pending
        }
        holder.tvStatus.background.setTint(holder.itemView.context.getColor(colorRes))

        holder.itemView.setOnClickListener { onItemClick(order) }
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
