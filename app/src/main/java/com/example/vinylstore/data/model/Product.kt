package com.example.vinylstore.data.model

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",
    val name: String = "",
    val artist: String = "",
    val genre: String = "",
    val year: Int = 0,
    val price: Double = 0.0,
    val description: String = "",
    val imageUrls: List<String> = emptyList(),  // МАССИВ ФОТО вместо одного
    val format: String = "Винил",
    val condition: String = "",
    val isNew: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)