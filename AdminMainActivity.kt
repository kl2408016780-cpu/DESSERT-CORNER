package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        val cardAddDessert = findViewById<CardView>(R.id.card_add_dessert)
        val cardManageStock = findViewById<CardView>(R.id.card_manage_stock)
        val cardViewOrders = findViewById<CardView>(R.id.card_view_orders)
        val cardLogout = findViewById<CardView>(R.id.card_logout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.adminBottomNavigation)

        bottomNav.selectedItemId = R.id.nav_admin_dash
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dash -> true
                R.id.nav_admin_inventory -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                R.id.nav_admin_orders -> {
                    startActivity(Intent(this, AdminOrdersActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Add New Dessert
        cardAddDessert.setOnClickListener {
            startActivity(Intent(this, AddEditDessertActivity::class.java))
        }

        // Manage Stock (Opens the Inventory List)
        cardManageStock.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        // View All Orders
        cardViewOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }

        // Logout with confirmation
        cardLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply()
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
