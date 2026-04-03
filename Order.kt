package com.example.dessertcorner4

data class Order(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 3.0,
    val total: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending",
    val paymentMethod: String = "Cash on Delivery",
    val deliveryAddress: String = "123 Sweet Street, Dessert Valley"
)
