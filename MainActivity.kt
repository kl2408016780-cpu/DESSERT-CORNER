package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var rvPopularTreats: RecyclerView
    private lateinit var dessertAdapter: DessertAdapter
    private lateinit var dessertManager: DessertManager
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dessertManager = DessertManager.getInstance(this)
        
        setupViews()
        setupBottomNavigation()
        loadUserData()
        loadDesserts()
    }

    private fun setupViews() {
        rvPopularTreats = findViewById(R.id.rvPopularTreats)
        etSearch = findViewById(R.id.etSearch)
        val tvSeeAllPopular = findViewById<TextView>(R.id.tvSeeAllPopular)

        dessertAdapter = DessertAdapter(mutableListOf()) { dessert ->
            addToCart(dessert)
        }

        rvPopularTreats.layoutManager = GridLayoutManager(this, 2)
        rvPopularTreats.adapter = dessertAdapter

        etSearch.addTextChangedListener { text ->
            filterDesserts(text.toString())
        }

        tvSeeAllPopular.setOnClickListener {
            startActivity(Intent(this, AllDessertsActivity::class.java))
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnBannerOrder).setOnClickListener {
            val intent = Intent(this, AllDessertsActivity::class.java)
            intent.putExtra("category", "Cakes")
            startActivity(intent)
        }
    }

    private fun loadDesserts() {
        // CORRECT: Use getDesserts()
        val desserts = dessertManager.getDesserts()
        dessertAdapter.updateData(desserts)
    }

    private fun filterDesserts(query: String) {
        val filtered = dessertManager.getDesserts().filter {
            it.name?.contains(query, ignoreCase = true) == true || 
            it.description?.contains(query, ignoreCase = true) == true
        }
        dessertAdapter.updateData(filtered)
    }
    

    private fun addToCart(dessert: Dessert) {
        CartManager.getInstance(this).addToCart(dessert, 1)
        Toast.makeText(this, "${dessert.name} added to cart!", Toast.LENGTH_SHORT).show()
        updateCartBadge()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    false
                }
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    false
                }
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

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Guest")
        // tvLocation removed for minimalist look
    }

    override fun onResume() {
        super.onResume()
        loadDesserts() 
        loadUserData()
        updateCartBadge()
        
        // Reset bottom nav selection
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_home
    }
}
