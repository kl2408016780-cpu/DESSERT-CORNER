package com.example.dessertcorner4

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager private constructor(context: Context) {
    private var cartItems: MutableList<CartItem> = mutableListOf()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        loadCart()
    }

    private fun loadCart() {
        val json = sharedPreferences.getString(CART_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            cartItems = gson.fromJson(json, type) ?: mutableListOf()
        }
    }

    private fun saveCart() {
        val json = gson.toJson(cartItems)
        sharedPreferences.edit().putString(CART_KEY, json).apply()
    }

    fun addToCart(dessert: Dessert, quantity: Int) {
        val existingItem = cartItems.find { it.dessert?.id == dessert.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(dessert, quantity))
        }
        saveCart()
    }

    fun removeFromCart(dessertId: String) {
        cartItems.removeAll { it.dessert?.id == dessertId }
        saveCart()
    }

    fun updateQuantity(dessertId: String, quantity: Int) {
        val item = cartItems.find { it.dessert?.id == dessertId }
        if (item != null) {
            if (quantity <= 0) {
                cartItems.remove(item)
            } else {
                item.quantity = quantity
            }
            saveCart()
        }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    fun getSubtotal(): Double {
        return cartItems.sumOf { (it.dessert?.price ?: 0.0) * it.quantity }
    }

    fun getItemCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
        saveCart()
    }

    companion object {
        private var instance: CartManager? = null
        private const val CART_PREFS = "cart_prefs"
        private const val CART_KEY = "cart_key"

        @Synchronized
        fun getInstance(context: Context): CartManager {
            if (instance == null) {
                instance = CartManager(context.applicationContext)
            }
            return instance!!
        }
    }
}
