package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.dessertcorner4.CartManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private lateinit var cartManager: CartManager

    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartManager = CartManager.getInstance(this)

        setupToolbar()
        setupBottomNavigation()
        setupRecyclerView()
        updateCartUI()

        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            if (cartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CheckoutActivity::class.java)
            val subtotal = cartManager.getSubtotal()
            val delivery = 3.0
            intent.putExtra("subtotal", subtotal)
            intent.putExtra("delivery", delivery)
            intent.putExtra("total", subtotal + delivery)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.tvContinueShopping).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        val rvCartItems = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvCartItems)
        cartAdapter = CartAdapter(cartManager.getCartItems()) { dessertId, newQuantity ->
            cartManager.updateQuantity(dessertId, newQuantity)
            updateCartUI()
        }
        rvCartItems.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvCartItems.adapter = cartAdapter
    }

    private fun updateCartUI() {
        val items = cartManager.getCartItems()
        
        // Handle Empty State
        val layoutEmptyCart = findViewById<View>(R.id.layoutEmptyCart)
        val rvCartItems = findViewById<View>(R.id.rvCartItems)
        val cardSummary = findViewById<View>(R.id.cardSummary)
        val btnCheckout = findViewById<View>(R.id.btnCheckout)

        if (items.isEmpty()) {
            layoutEmptyCart.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            cardSummary.visibility = View.GONE
            btnCheckout.visibility = View.GONE
        } else {
            layoutEmptyCart.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            cardSummary.visibility = View.VISIBLE
            btnCheckout.visibility = View.VISIBLE
            
            cartAdapter.updateData(items)
        }

        val subtotal = cartManager.getSubtotal()
        val deliveryFee = if (items.isEmpty()) 0.0 else 3.0
        val total = subtotal + deliveryFee

        findViewById<TextView>(R.id.tvSubtotal).text = "RM${String.format("%.2f", subtotal)}"
        findViewById<TextView>(R.id.tvDeliveryFee).text = "RM${String.format("%.2f", deliveryFee)}"
        findViewById<TextView>(R.id.tvTotal).text = String.format("%.2f", total)
        updateCartBadge()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_cart
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.nav_cart -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    false
                }
                else -> false
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
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_cart
        updateCartBadge()
    }
}
