package com.example.dessertcorner4

class Dessert {
    // Getters and Setters
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var price: Double = 0.0
    var category: String? = null
    var imageUrl: String? = null
    var stock: Int = 0
    var rating: Double = 0.0
    var isPopular: Boolean = false

    constructor()

    constructor(name: String?, description: String?, price: Double, category: String?, stock: Int, imageUrl: String? = null) {
        this.name = name
        this.description = description
        this.price = price
        this.category = category
        this.stock = stock
        this.imageUrl = imageUrl
        this.rating = 0.0
        this.isPopular = false
    }
}