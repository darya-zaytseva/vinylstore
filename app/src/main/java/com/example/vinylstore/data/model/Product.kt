package com.example.vinylstore.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
data class Product(
    val id: String = "",
    val name: String = "",
    val artist: String = "",
    val genre: String = "",
    val year: Int = 0,
    val price: Double = 0.0,
    val description: String = "",
    val format: String = "Винил",
    val condition: String = "",
    val isNew: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    // Приватные поля из Firestore
    @get:PropertyName("imageUrl")
    @set:PropertyName("imageUrl")
    var imageUrl: String = "",
    @get:PropertyName("imageUrl2")
    @set:PropertyName("imageUrl2")
    var imageUrl2: String = ""
) {
    // Вычисляемое свойство — собираем массив из двух полей
    val imageUrls: List<String>
        get() = listOfNotNull(
            imageUrl.takeIf { it.isNotBlank() },
            imageUrl2.takeIf { it.isNotBlank() }
        )
}