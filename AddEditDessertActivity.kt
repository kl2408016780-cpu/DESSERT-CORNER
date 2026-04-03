package com.example.dessertcorner4

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AddEditDessertActivity : AppCompatActivity() {

    private lateinit var dessertManager: DessertManager
    private var dessertId: String? = null
    private var isEditing = false

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var ivDessertImage: ImageView
    
    private var selectedImageUrl: String? = null
    
    private val categories = arrayOf("Cakes", "Pastries", "Cookies", "Beverages")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dessert)

        dessertManager = DessertManager.getInstance(this)

        initViews()
        setupCategorySpinner()

        // Handle Intent Data
        dessertId = intent.getStringExtra("DESSERT_ID")
        if (dessertId != null) {
            isEditing = true
            findViewById<TextView>(R.id.tvAddDessertTitle)?.text = "Edit Dessert"
            loadDessertData(dessertId!!)
        }

        setupListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.etDessertName)
        etPrice = findViewById(R.id.etPrice)
        etStock = findViewById(R.id.etStock)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        ivDessertImage = findViewById(R.id.ivDessertImage)
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnClose).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            validateAndSave()
        }

        findViewById<Button>(R.id.btnUploadImage).setOnClickListener {
            showImageUrlDialog()
        }
    }

    private fun validateAndSave() {
        val name = etName.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        val stockStr = etStock.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 0
        
        val dessert = Dessert(name, description, price, category, stock, selectedImageUrl).apply {
            if (isEditing) this.id = dessertId
        }

        if (isEditing) {
            dessertManager.updateDessert(dessert)
        } else {
            dessertManager.addDessert(dessert)
        }

        Toast.makeText(this, "Dessert saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun loadDessertData(id: String) {
        val dessert = dessertManager.getDessertById(id)
        dessert?.let {
            etName.setText(it.name)
            etPrice.setText(it.price.toString())
            etStock.setText(it.stock.toString())
            etDescription.setText(it.description)
            selectedImageUrl = it.imageUrl
            loadDessertImage(it.imageUrl)
            
            val pos = categories.indexOf(it.category)
            if (pos >= 0) {
                spinnerCategory.setSelection(pos)
            }
        }
    }

    private fun showImageUrlDialog() {
        val input = EditText(this)
        input.hint = "Paste image URL here (Unsplash, etc.)"
        input.setText(selectedImageUrl ?: "")

        AlertDialog.Builder(this)
            .setTitle("Dessert Image URL")
            .setMessage("Please enter the public URL for the dessert image:")
            .setView(input)
            .setPositiveButton("Preview") { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotEmpty()) {
                    selectedImageUrl = url
                    loadDessertImage(url)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadDessertImage(url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_upload)
                .error(R.drawable.ic_upload)
                .into(ivDessertImage)
        }
    }
}
