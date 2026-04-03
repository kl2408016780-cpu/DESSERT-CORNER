package com.example.dessertcorner4

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dessertcorner4.CartManager
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var cartManager: CartManager
    private var statusListener: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        cartManager = CartManager.getInstance(this)

        setupOrderSummary()
        
        findViewById<MaterialButton>(R.id.btnBackToHome).setOnClickListener {
            // Clear cart and go back to home
            cartManager.clearCart()
            
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }

    private fun setupOrderSummary() {
        val orderId = intent.getStringExtra("order_id") ?: ""
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        statusListener = firestore.collection("orders").document(orderId)
            .addSnapshotListener { document, error ->
                if (error != null) return@addSnapshotListener
                
                if (document != null && document.exists()) {
                    val order = document.toObject(Order::class.java)
                    if (order != null) {
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        findViewById<TextView>(R.id.tvOrderNumber).text = order.id
                        findViewById<TextView>(R.id.tvOrderDate).text = sdf.format(Date(order.timestamp))
                        findViewById<TextView>(R.id.tvSummaryAddress).text = order.deliveryAddress
                        val tvStatus = findViewById<TextView>(R.id.tvSummaryStatus)
                        tvStatus.text = order.status.uppercase()
                        val colorRes = when(order.status) {
                            "Preparing" -> R.color.status_preparing
                            "Out for Delivery" -> R.color.status_out_for_delivery
                            "Delivered" -> R.color.status_delivered
                            "Cancelled" -> R.color.status_cancelled
                            else -> R.color.status_pending
                        }
                        tvStatus.background.setTint(getColor(colorRes))

                    val layoutItems = findViewById<LinearLayout>(R.id.layoutOrderItems)
                    layoutItems.removeAllViews()

                    for (item in order.items) {
                        val itemLayout = LinearLayout(this)
                        itemLayout.orientation = LinearLayout.HORIZONTAL
                        itemLayout.setPadding(0, 8, 0, 8)
                        
                        val tvName = TextView(this)
                        tvName.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        tvName.text = item.dessert?.name
                        tvName.setTextColor(getColor(R.color.login_primary))
                        tvName.textSize = 14f

                        val tvQty = TextView(this)
                        tvQty.layoutParams = LinearLayout.LayoutParams(pixelsToDp(60), LinearLayout.LayoutParams.WRAP_CONTENT)
                        tvQty.text = item.quantity.toString()
                        tvQty.gravity = Gravity.CENTER
                        tvQty.setTextColor(getColor(R.color.login_primary))
                        tvQty.textSize = 14f

                        val tvPrice = TextView(this)
                        tvPrice.layoutParams = LinearLayout.LayoutParams(pixelsToDp(80), LinearLayout.LayoutParams.WRAP_CONTENT)
                        tvPrice.text = "RM${String.format("%.2f", (item.dessert?.price ?: 0.0) * item.quantity)}"
                        tvPrice.gravity = Gravity.END
                        tvPrice.setTextColor(getColor(R.color.login_primary))
                        tvPrice.textSize = 14f
                        tvPrice.setTypeface(null, Typeface.BOLD)

                        itemLayout.addView(tvName)
                        itemLayout.addView(tvQty)
                        itemLayout.addView(tvPrice)
                        layoutItems.addView(itemLayout)
                    }

                    findViewById<TextView>(R.id.tvSummarySubtotal).text = "RM${String.format("%.2f", order.subtotal)}"
                        findViewById<TextView>(R.id.tvSummaryTotal).text = "RM${String.format("%.2f", order.total)}"
                    }
                } else {
                    Toast.makeText(this, "Order details not found!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        statusListener?.remove()
    }

    private fun pixelsToDp(pixels: Int): Int {
        val scale = resources.displayMetrics.density
        return (pixels * scale + 0.5f).toInt()
    }
}
