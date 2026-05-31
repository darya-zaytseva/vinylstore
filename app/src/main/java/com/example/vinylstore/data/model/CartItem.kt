package com.example.vinylstore.data.model

data class CartItem(
    val productId: String = "",
    val name: String = "",
    val artist: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",  // Первое фото для корзины
    val quantity: Int = 1
)