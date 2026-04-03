package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupToolbar()
        loadUserData()
        setupBottomNavigation()
        setupListeners()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadUserData() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()
        val orderManager = OrderManager.getInstance(this)

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    findViewById<TextView>(R.id.tvProfileName).text = user.fullName
                    findViewById<TextView>(R.id.tvProfileEmail).text = user.email

                    saveUserToPrefs(user)

                    val btnAdmin = findViewById<TextView>(R.id.btnAdminPanel)
                    if (user.userType == "admin" || user.email == "admin@gmail.com") {
                        btnAdmin.visibility = android.view.View.VISIBLE
                    } else {
                        btnAdmin.visibility = android.view.View.GONE
                    }
                    
                    updateOrderStats(orderManager, userId)
                    
                    // Listen for real-time updates to orders
                    orderManager.addListener {
                        runOnUiThread { updateOrderStats(orderManager, userId) }
                    }
                }
            }
    }

    private fun updateOrderStats(orderManager: OrderManager, userId: String) {
        val orders = orderManager.getOrdersForUser(userId)
        val count = orders.size
        val spent = orders.sumOf { it.total }
        
        findViewById<TextView>(R.id.tvOrderCount).text = count.toString()
        findViewById<TextView>(R.id.tvTotalSpent).text = "RM${String.format("%.0f", spent)}"
    }

    private fun saveUserToPrefs(user: User) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("user_id", user.id)
            putString("user_name", user.fullName)
            putString("user_email", user.email)
            putString("user_phone", user.phone)
            putString("user_type", user.userType)
            apply()
        }
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.btnAdminPanel).setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        findViewById<TextView>(R.id.btnOrdersHistory).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<TextView>(R.id.btnEditProfile).setOnClickListener {
            showEditProfileDialog()
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply()
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditProfileDialog() {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val currentName = sharedPref.getString("user_name", "")
        val currentEmail = sharedPref.getString("user_email", "")
        val currentPhone = sharedPref.getString("user_phone", "")

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditName)
        val etEmail = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditEmail)
        val etPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPhone)

        etName.setText(currentName)
        etEmail.setText(currentEmail)
        etPhone.setText(currentPhone)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = etName.text.toString().trim()
                val newEmail = etEmail.text.toString().trim()
                val newPhone = etPhone.text.toString().trim()

                if (newName.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show()
                } else {
                    val auth = FirebaseAuth.getInstance()
                    val userId = auth.currentUser?.uid ?: return@setPositiveButton
                    val firestore = FirebaseFirestore.getInstance()

                    val updates = mapOf(
                        "fullName" to newName,
                        "email" to newEmail,
                        "phone" to newPhone
                    )

                    firestore.collection("users").document(userId).update(updates)
                        .addOnSuccessListener {
                            loadUserData()
                            Toast.makeText(this, "Profile synced with Firestore", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Firestore Sync failed!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    true
                }
                R.id.nav_profile -> true
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
        findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_profile
        updateCartBadge()
    }
}
