package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AllDessertsActivity : AppCompatActivity() {

    private lateinit var rvAllDesserts: RecyclerView
    private lateinit var dessertAdapter: DessertAdapter
    private lateinit var dessertManager: DessertManager
    private lateinit var etSearch: EditText
    
    private var currentCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_desserts)

        dessertManager = DessertManager.getInstance(this)
        
        val categoryFromIntent = intent.getStringExtra("category")
        if (categoryFromIntent != null) {
            currentCategory = categoryFromIntent
        }

        setupViews()
        
        if (currentCategory != "All") {
            updateCategoryUI(currentCategory)
            filterByCategory(currentCategory)
        } else {
            loadDesserts()
        }
    }

    private fun setupViews() {
        rvAllDesserts = findViewById(R.id.rvAllDesserts)
        etSearch = findViewById(R.id.etSearch)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val btnHeaderCart = findViewById<androidx.cardview.widget.CardView>(R.id.btnHeaderCart)

        dessertAdapter = DessertAdapter(mutableListOf()) { dessert ->
            addToCart(dessert)
        }

        rvAllDesserts.layoutManager = GridLayoutManager(this, 2)
        rvAllDesserts.adapter = dessertAdapter

        ivBack.setOnClickListener { finish() }
        
        btnHeaderCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        etSearch.addTextChangedListener { text ->
            filterDesserts(text.toString())
        }

        // Category clicks
        findViewById<TextView>(R.id.catAll).setOnClickListener { 
            updateCategoryUI("All")
            loadDesserts() 
        }
        findViewById<TextView>(R.id.catCake).setOnClickListener { 
            updateCategoryUI("Cakes")
            filterByCategory("Cakes") 
        }
        findViewById<TextView>(R.id.catPastries).setOnClickListener { 
            updateCategoryUI("Pastries")
            filterByCategory("Pastries") 
        }
        findViewById<TextView>(R.id.catCookies).setOnClickListener { 
            updateCategoryUI("Cookies")
            filterByCategory("Cookies") 
        }
        findViewById<TextView>(R.id.catBeverages).setOnClickListener { 
            updateCategoryUI("Beverages")
            filterByCategory("Beverages") 
        }
    }

    private fun updateCategoryUI(category: String) {
        currentCategory = category
        
        val categoryId = when (category) {
            "Cakes" -> R.id.catCake
            "Pastries", "Pastry" -> R.id.catPastries
            "Cookies" -> R.id.catCookies
            "Beverages" -> R.id.catBeverages
            else -> R.id.catAll
        }

        val cats = listOf(R.id.catAll, R.id.catCake, R.id.catPastries, R.id.catCookies, R.id.catBeverages)
        
        cats.forEach { id ->
            val view = findViewById<TextView>(id)
            if (id == categoryId) {
                view.setBackgroundResource(R.drawable.bg_active_category)
                view.setTextColor(resources.getColor(R.color.white, null))
            } else {
                view.setBackgroundResource(R.drawable.bg_inactive_category)
                view.setTextColor(resources.getColor(R.color.login_primary, null))
            }
        }
    }

    private fun loadDesserts() {
        if (currentCategory == "All") {
            val desserts = dessertManager.getDesserts()
            dessertAdapter.updateData(desserts)
        } else {
            filterByCategory(currentCategory)
        }
    }

    private fun filterDesserts(query: String) {
        val filtered = dessertManager.getDesserts().filter {
            (it.name?.contains(query, ignoreCase = true) == true || 
             it.description?.contains(query, ignoreCase = true) == true) &&
            (currentCategory == "All" || it.category == currentCategory)
        }
        dessertAdapter.updateData(filtered)
    }

    private fun filterByCategory(category: String) {
        val filtered = dessertManager.getDesserts().filter {
            it.category == category
        }
        dessertAdapter.updateData(filtered)
    }

    private fun addToCart(dessert: Dessert) {
        CartManager.getInstance(this).addToCart(dessert, 1)
        Toast.makeText(this, "${dessert.name} added to cart!", Toast.LENGTH_SHORT).show()
        updateCartBadge()
    }

    private fun updateCartBadge() {
        // Find badge TextView (need to add to layout)
        val tvCartBadge = findViewById<TextView>(R.id.tvCartBadge) ?: return
        val cartCount = CartManager.getInstance(this).getCartItems().size
        
        if (cartCount > 0) {
            tvCartBadge.visibility = android.view.View.VISIBLE
            tvCartBadge.text = cartCount.toString()
        } else {
            tvCartBadge.visibility = android.view.View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        loadDesserts()
        updateCategoryUI(currentCategory)
        updateCartBadge()
    }
}
