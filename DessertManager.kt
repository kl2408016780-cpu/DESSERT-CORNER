package com.example.dessertcorner4

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DessertManager private constructor(context: Context) {
    private var desserts: MutableList<Dessert> = mutableListOf()
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("desserts")
    
    private val listeners = mutableListOf<() -> Unit>()

    init {
        startSyncing()
    }

    private fun startSyncing() {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            
            if (snapshot != null) {
                val newList = mutableListOf<Dessert>()
                for (doc in snapshot.documents) {
                    doc.toObject(Dessert::class.java)?.let { newList.add(it) }
                }

                if (newList.isEmpty()) {
                    addDefaultDessertsToFirestore()
                } else {
                    desserts = newList
                    notifyListeners()
                }
            }
        }
    }

    private fun addDefaultDessertsToFirestore() {
        val defaults = listOf(
            Dessert("Choco Lava Delight", "Warm molten dark chocolate cake", 8.50, "Cakes", 10).apply { id = "choco_lava" },
            Dessert("Berry Cheesecake", "Creamy NY style with fresh berries", 7.25, "Cakes", 15).apply { id = "strawberry_cheesecake" },
            Dessert("Rainbow Macarons", "Box of 6 colorful french macarons", 12.00, "Pastries", 20).apply { id = "macarons_set" }
        )
        
        for (d in defaults) {
            d.id?.let { id -> collection.document(id).set(d) }
        }
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it() }
    }

    fun getDesserts(): List<Dessert> = desserts

    fun getDessertById(id: String): Dessert? = desserts.find { it.id == id }

    fun addDessert(dessert: Dessert) {
        val docRef = if (dessert.id.isNullOrEmpty()) collection.document() else collection.document(dessert.id!!)
        dessert.id = docRef.id
        docRef.set(dessert)
    }

    fun updateDessert(dessert: Dessert) {
        dessert.id?.let { collection.document(it).set(dessert) }
    }

    fun deleteDessert(id: String) {
        collection.document(id).delete()
    }

    companion object {
        private var instance: DessertManager? = null

        @Synchronized
        fun getInstance(context: Context): DessertManager {
            if (instance == null) {
                instance = DessertManager(context.applicationContext)
            }
            return instance!!
        }
    }
}
