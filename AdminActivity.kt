package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

    private lateinit var rvInventory: RecyclerView
    private lateinit var tvActiveItems: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var btnAddNew: MaterialCardView
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var adapter: AdminDessertAdapter
    private val dessertList: MutableList<Dessert> = mutableListOf()
    private lateinit var dessertManager: DessertManager
    private var currentCategory = "All Items"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dessertManager = DessertManager.getInstance(this)

        initViews()
        setupTabs()
        setupListeners()
        loadInventory()
        setupBottomNavigation()
    }

    private fun initViews() {
        rvInventory = findViewById(R.id.rvInventory)
        tvActiveItems = findViewById(R.id.tvActiveItems)
        tabLayout = findViewById(R.id.tabLayout)
        btnAddNew = findViewById(R.id.btnAddNew)
        bottomNav = findViewById(R.id.adminBottomNavigation)

        adapter = AdminDessertAdapter(this, dessertList)
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = adapter
    }

    private fun setupBottomNavigation() {
        bottomNav.selectedItemId = R.id.nav_admin_inventory
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dash -> {
                    startActivity(Intent(this, AdminMainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_admin_inventory -> true
                R.id.nav_admin_orders -> {
                    startActivity(Intent(this, AdminOrdersActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All Items"))
        tabLayout.addTab(tabLayout.newTab().setText("Cakes"))
        tabLayout.addTab(tabLayout.newTab().setText("Pastries"))
        tabLayout.addTab(tabLayout.newTab().setText("Cookies"))
        tabLayout.addTab(tabLayout.newTab().setText("Beverages"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.text?.let {
                    currentCategory = it.toString()
                    loadInventory()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadInventory() {
        val allDesserts = dessertManager.getDesserts()
        dessertList.clear()
        
        for (dessert in allDesserts) {
            if (currentCategory == "All Items" || dessert.category == currentCategory) {
                dessertList.add(dessert)
            }
        }
        
        adapter.notifyDataSetChanged()
        tvActiveItems.text = "${dessertList.size} active items in shop"
    }

    private fun setupListeners() {
        btnAddNew.setOnClickListener {
            val intent = Intent(this, AddEditDessertActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialCardView>(R.id.ivBack)?.setOnClickListener {
            startActivity(Intent(this, AdminMainActivity::class.java))
            finish()
        }

        adapter.setOnItemClickListener(object : AdminDessertAdapter.OnItemClickListener {
            override fun onEditClick(dessert: Dessert) {
                val intent = Intent(this@AdminActivity, AddEditDessertActivity::class.java)
                intent.putExtra("DESSERT_ID", dessert.id)
                startActivity(intent)
            }

            override fun onDeleteClick(dessert: Dessert) {
                deleteDessert(dessert)
            }
        })
    }

    private fun deleteDessert(dessert: Dessert) {
        AlertDialog.Builder(this)
            .setTitle("Delete Dessert")
            .setMessage("Are you sure you want to delete '${dessert.name}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                val id = dessert.id ?: return@setPositiveButton
                dessertManager.deleteDessert(id)
                Toast.makeText(this, "${dessert.name} deleted", Toast.LENGTH_SHORT).show()
                loadInventory()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadInventory()
    }
}
