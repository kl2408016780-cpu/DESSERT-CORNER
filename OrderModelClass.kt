package com.example.dessertcorner4

import java.io.Serializable

class OrderModelClass : Serializable {
    var orderId: String? = null
    var userId: String? = null
    var userName: String? = null
    var items: MutableList<CartItem>? = null
    var subtotal: Double = 0.0
    var deliveryFee: Double = 0.0
    var total: Double = 0.0
    var deliveryAddress: String? = null
    var paymentMethod: String? = null
    var deliveryInstructions: String? = null
    var status: String? = null
    var orderDate: Long = 0

    constructor()

    constructor(
        orderId: String?,
        userId: String?,
        userName: String?,
        items: MutableList<CartItem>?,
        subtotal: Double,
        deliveryFee: Double,
        total: Double,
        deliveryAddress: String?,
        paymentMethod: String?,
        deliveryInstructions: String?,
        status: String?,
        orderDate: Long
    ) {
        this.orderId = orderId
        this.userId = userId
        this.userName = userName
        this.items = items
        this.subtotal = subtotal
        this.deliveryFee = deliveryFee
        this.total = total
        this.deliveryAddress = deliveryAddress
        this.paymentMethod = paymentMethod
        this.deliveryInstructions = deliveryInstructions
        this.status = status
        this.orderDate = orderDate
    }
}
