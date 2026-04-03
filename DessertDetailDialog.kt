package com.example.dessertcorner4

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import java.util.Locale

object DessertDetailDialog {
    const val PRODUCT_STRAWBERRY_CHEESECAKE = 0
    const val PRODUCT_CHOCOLATE_LAVA_CAKE = 1
    const val PRODUCT_MACARON_SET = 2
    const val PRODUCT_MATCHA_ICE_CREAM = 3

    private fun getName(productId: Int) = when (productId) {
        PRODUCT_STRAWBERRY_CHEESECAKE -> "Strawberry Cheesecake"
        PRODUCT_CHOCOLATE_LAVA_CAKE -> "Chocolate Lava Cake"
        PRODUCT_MACARON_SET -> "Macaron Set"
        PRODUCT_MATCHA_ICE_CREAM -> "Matcha Ice Cream"
        else -> ""
    }

    private fun getPrice(productId: Int) = when (productId) {
        PRODUCT_STRAWBERRY_CHEESECAKE -> 15.00
        PRODUCT_CHOCOLATE_LAVA_CAKE -> 10.00
        PRODUCT_MACARON_SET -> 12.00
        PRODUCT_MATCHA_ICE_CREAM -> 10.00
        else -> 0.0
    }

    private fun getDescription(productId: Int) = when (productId) {
        PRODUCT_STRAWBERRY_CHEESECAKE -> "A rich and creamy cheesecake topped with fresh strawberries."
        PRODUCT_CHOCOLATE_LAVA_CAKE -> "A warm chocolate cake with a gooey molten centre."
        PRODUCT_MACARON_SET -> "A delightful set of French macarons in assorted flavours."
        PRODUCT_MATCHA_ICE_CREAM -> "Smooth and creamy Japanese matcha ice cream."
        else -> ""
    }

    private fun getImage(productId: Int) = when (productId) {
        PRODUCT_STRAWBERRY_CHEESECAKE -> R.drawable.strawberry_cheesecake
        PRODUCT_CHOCOLATE_LAVA_CAKE -> R.drawable.choco_lava
        PRODUCT_MACARON_SET -> R.drawable.macarons_set
        PRODUCT_MATCHA_ICE_CREAM -> R.drawable.matcha_icecream
        else -> 0
    }

    fun show(context: Context, productId: Int) {
        Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_dessert_detail)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val ivImage = findViewById<ImageView>(R.id.iv_dialog_image)
            val tvName = findViewById<TextView>(R.id.tv_dialog_name)
            val tvPrice = findViewById<TextView>(R.id.tv_dialog_price)
            val tvDesc = findViewById<TextView>(R.id.tv_dialog_description)
            val tvQuantity = findViewById<TextView>(R.id.tv_dialog_quantity)
            
            ivImage?.setImageResource(getImage(productId))
            tvName?.text = getName(productId)
            tvPrice?.text = String.format(Locale.getDefault(), "RM %.2f", getPrice(productId))
            tvDesc?.text = getDescription(productId)

            var qty = 1
            tvQuantity?.text = qty.toString()

            findViewById<TextView>(R.id.btn_minus)?.setOnClickListener {
                if (qty > 1) {
                    qty--
                    tvQuantity?.text = qty.toString()
                }
            }

            findViewById<TextView>(R.id.btn_plus)?.setOnClickListener {
                qty++
                tvQuantity?.text = qty.toString()
            }

            findViewById<CardView>(R.id.card_close_dialog)?.setOnClickListener { dismiss() }

            findViewById<CardView>(R.id.card_dialog_add_to_cart)?.setOnClickListener {
                val dessert = Dessert(getName(productId), getDescription(productId), getPrice(productId), "Category", 100).apply {
                    id = productId.toString()
                }
                CartManager.getInstance(context).addToCart(dessert, qty)
                Toast.makeText(context, "$qty x ${dessert.name} added to cart!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            
            show()
        }
    }
}
