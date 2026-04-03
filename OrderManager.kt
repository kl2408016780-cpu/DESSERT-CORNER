package com.example.dessertcorner4

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrderManager private constructor(context: Context) {

    private var allOrders: MutableList<Order> = mutableListOf()
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("orders")
    private val listeners = mutableListOf<() -> Unit>()

    init {
        startSyncing()
    }

    private fun startSyncing() {
        collection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                if (snapshot != null) {
                    val newList = mutableListOf<Order>()
                    for (doc in snapshot.documents) {
                        doc.toObject(Order::class.java)?.let { newList.add(it) }
                    }
                    allOrders = newList
                    notifyListeners()
                }
            }
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it() }
    }

    companion object {
        @Volatile
        private var instance: OrderManager? = null

        fun getInstance(context: Context): OrderManager {
            return instance ?: synchronized(this) {
                instance ?: OrderManager(context).also { instance = it }
            }
        }
    }

    fun saveOrder(order: Order) {
        val docRef = if (order.id.isEmpty()) collection.document() else collection.document(order.id)
        val finalOrder = order.copy(id = docRef.id)
        docRef.set(finalOrder)
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        collection.document(orderId).update("status", newStatus)
    }

    fun getAllOrders(): List<Order> = allOrders

    fun getOrdersForUser(userId: String): List<Order> {
        return allOrders.filter { it.userId == userId }
    }
}
