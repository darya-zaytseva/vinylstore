package com.example.vinylstore.data.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "Создан",
    val createdAt: Timestamp = Timestamp.now(),
    val trackNumber: String = ""
)