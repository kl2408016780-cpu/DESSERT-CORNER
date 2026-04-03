package com.example.dessertcorner4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText

class CheckoutActivity : AppCompatActivity() {

    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvCheckoutAddress: TextView
    private lateinit var etCardNumber: TextInputEditText
    private lateinit var etExpiry: TextInputEditText
    private lateinit var etCvv: TextInputEditText
    private lateinit var etInstructions: TextInputEditText
    private lateinit var layoutCardDetails: LinearLayout
    private lateinit var radioGroupPayment: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initViews()
        setupToolbar()
        setupPaymentMethodListener()
        loadOrderSummary()
        setupClickListeners()
    }

    private fun initViews() {
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
        tvTotal = findViewById(R.id.tvTotal)
        tvCheckoutAddress = findViewById(R.id.tvCheckoutAddress)
        etCardNumber = findViewById(R.id.etCardNumber)
        etExpiry = findViewById(R.id.etExpiry)
        etCvv = findViewById(R.id.etCvv)
        etInstructions = findViewById(R.id.etInstructions)
        layoutCardDetails = findViewById(R.id.layoutCardDetails)
        radioGroupPayment = findViewById(R.id.radioGroupPayment)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupPaymentMethodListener() {
        radioGroupPayment.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbCard) {
                layoutCardDetails.visibility = View.VISIBLE
            } else {
                layoutCardDetails.visibility = View.GONE
            }
        }
    }

    private fun loadOrderSummary() {
        val subtotal = intent.getDoubleExtra("subtotal", 0.0)
        val deliveryFee = intent.getDoubleExtra("delivery", 0.0)
        val total = intent.getDoubleExtra("total", subtotal + deliveryFee)

        tvSubtotal.text = String.format("RM %.2f", subtotal)
        tvDeliveryFee.text = String.format("RM %.2f", deliveryFee)
        tvTotal.text = String.format("RM %.2f", total)
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.tvChangeAddress).setOnClickListener {
            showChangeAddressDialog()
        }

        findViewById<Button>(R.id.btnPlaceOrder).setOnClickListener {
            val selectedPaymentId = radioGroupPayment.checkedRadioButtonId
            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedPayment = findViewById<RadioButton>(selectedPaymentId)

            if (selectedPaymentId == R.id.rbCard) {
                if (etCardNumber.text.isNullOrEmpty() || etExpiry.text.isNullOrEmpty() || etCvv.text.isNullOrEmpty()) {
                    Toast.makeText(this, "Please fill in card details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Create and Save Order
            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val userName = sharedPref.getString("user_name", "Customer") ?: "Customer"
            val userEmail = sharedPref.getString("user_email", "") ?: ""
            val userId = sharedPref.getString("user_id", "") ?: ""
            
            val subtotal = intent.getDoubleExtra("subtotal", 0.0)
            val delivery = intent.getDoubleExtra("delivery", 0.0)
            val total = intent.getDoubleExtra("total", subtotal + delivery)
            val cartItems = CartManager.getInstance(this).getCartItems()
            
            val orderId = "OR-${System.currentTimeMillis().toString().takeLast(6)}"
            val newOrder = Order(
                id = orderId,
                userId = userId,
                userName = userName,
                userEmail = userEmail,
                items = cartItems.toList(),
                subtotal = subtotal,
                deliveryFee = delivery,
                total = total,
                paymentMethod = selectedPayment.text.toString(),
                deliveryAddress = tvCheckoutAddress.text.toString()
            )
            
            OrderManager.getInstance(this).saveOrder(newOrder)

            // Process order
            Toast.makeText(this, "Order placed successfully! 🎉", Toast.LENGTH_LONG).show()

            // Clear cart using singleton
            CartManager.getInstance(this).clearCart()

            // Go to success page
            val intent = Intent(this, OrderSummaryActivity::class.java)
            intent.putExtra("order_id", orderId)
            startActivity(intent)
            finish()
        }
    }

    private fun showChangeAddressDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Change Delivery Address")
        
        val input = android.widget.EditText(this)
        input.hint = "Enter your full address"
        input.setText(tvCheckoutAddress.text.toString())
        
        val container = LinearLayout(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(40, 20, 40, 20)
        input.layoutParams = params
        container.addView(input)
        
        builder.setView(container)
        
        builder.setPositiveButton("Save") { _, _ ->
            val newAddress = input.text.toString()
            if (newAddress.isNotEmpty()) {
                tvCheckoutAddress.text = newAddress
                Toast.makeText(this, "Address updated!", Toast.LENGTH_SHORT).show()
            }
        }
        
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        
        builder.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
