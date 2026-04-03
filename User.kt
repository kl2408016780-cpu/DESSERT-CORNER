package com.example.dessertcorner4

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val userType: String = "customer"
)