package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvNoOrders: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var orderManager: OrderManager
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        orderManager = OrderManager.getInstance(this)
        
        setupToolbar()
        initViews()
        setupBottomNavigation()
        loadOrderHistory()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        rvOrders = findViewById(R.id.rvOrderHistory)
        tvNoOrders = findViewById(R.id.tvNoOrdersHistory)
        bottomNav = findViewById(R.id.bottomNavigation)
        
        rvOrders.layoutManager = LinearLayoutManager(this)
        adapter = OrderHistoryAdapter(emptyList()) { order ->
            val intent = Intent(this, OrderSummaryActivity::class.java)
            intent.putExtra("order_id", order.id)
            startActivity(intent)
        }
        rvOrders.adapter = adapter
    }

    private fun setupBottomNavigation() {
        bottomNav.selectedItemId = R.id.nav_orders
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_orders -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadOrderHistory() {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", "") ?: ""
        
        val orders = orderManager.getOrdersForUser(userId)
        if (orders.isEmpty()) {
            tvNoOrders.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        } else {
            tvNoOrders.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            adapter.updateData(orders)
        }
        
        // Listen for real-time updates
        orderManager.addListener {
            val updatedOrders = orderManager.getOrdersForUser(userId)
            runOnUiThread {
                if (updatedOrders.isEmpty()) {
                    tvNoOrders.visibility = View.VISIBLE
                    rvOrders.visibility = View.GONE
                } else {
                    tvNoOrders.visibility = View.GONE
                    rvOrders.visibility = View.VISIBLE
                    adapter.updateData(updatedOrders)
                }
            }
        }
    }

    private fun updateCartBadge() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val cartCount = CartManager.getInstance(this).getCartItems().size
        
        if (cartCount > 0) {
            val badge = bottomNav.getOrCreateBadge(R.id.nav_cart)
            badge.isVisible = true
            badge.number = cartCount
            badge.backgroundColor = getColor(R.color.login_primary)
            badge.badgeTextColor = getColor(R.color.white)
        } else {
            bottomNav.removeBadge(R.id.nav_cart)
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_orders
        updateCartBadge()
    }
}
