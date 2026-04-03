package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class AdminOrdersActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvNoOrders: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var orderManager: OrderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders)

        orderManager = OrderManager.getInstance(this)
        
        setupToolbar()
        initViews()
        loadOrders()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { 
            startActivity(Intent(this, AdminMainActivity::class.java))
            finish()
        }
    }

    private fun initViews() {
        rvOrders = findViewById(R.id.rvAdminOrders)
        tvNoOrders = findViewById(R.id.tvNoOrders)
        bottomNav = findViewById(R.id.adminBottomNavigation)
        rvOrders.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBottomNavigation() {
        bottomNav.selectedItemId = R.id.nav_admin_orders
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dash -> {
                    startActivity(Intent(this, AdminMainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_admin_inventory -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_admin_orders -> true
                else -> false
            }
        }
    }

    private fun loadOrders() {
        val orders = orderManager.getAllOrders()
        if (orders.isEmpty()) {
            tvNoOrders.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        } else {
            tvNoOrders.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            rvOrders.adapter = AdminOrderAdapter(orders, orderManager)
        }
    }

    class AdminOrderAdapter(private val orders: List<Order>, private val orderManager: OrderManager) : RecyclerView.Adapter<AdminOrderAdapter.ViewHolder>() {
        
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
            val tvCustomerName: TextView = view.findViewById(R.id.tvCustomerName)
            val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
            val tvOrderItems: TextView = view.findViewById(R.id.tvOrderItems)
            val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
            val tvStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_order, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val order = orders[position]
            holder.tvOrderId.text = "#${order.id}"
            holder.tvCustomerName.text = "Customer: ${order.userName}"
            
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            holder.tvOrderDate.text = sdf.format(Date(order.timestamp))
            
            val itemsString = order.items.joinToString(", ") { "${it.dessert?.name} x${it.quantity}" }
            holder.tvOrderItems.text = itemsString
            holder.tvOrderTotal.text = "RM ${String.format("%.2f", order.total)}"
            holder.tvStatus.text = order.status.uppercase()
            val colorRes = when(order.status) {
                "Preparing" -> R.color.status_preparing
                "Out for Delivery" -> R.color.status_out_for_delivery
                "Delivered" -> R.color.status_delivered
                "Cancelled" -> R.color.status_cancelled
                else -> R.color.status_pending
            }
            holder.tvStatus.background.setTint(holder.itemView.context.getColor(colorRes))
            
            holder.tvStatus.setOnClickListener {
                val statuses = arrayOf("Pending", "Preparing", "Out for Delivery", "Delivered", "Cancelled")
                androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Update Order Status")
                    .setItems(statuses) { _, which ->
                        orderManager.updateOrderStatus(order.id, statuses[which])
                    }
                    .show()
            }
        }

        override fun getItemCount() = orders.size
    }
}
