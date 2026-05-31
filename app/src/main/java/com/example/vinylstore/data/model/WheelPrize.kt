package com.example.vinylstore.data.model

data class WheelPrize(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "discount", // discount, free_vinyl, bonus_points
    val value: Int = 0, // процент скидки или количество баллов
    val productId: String = "", // если приз — конкретная пластинка
    val productName: String = "",
    val productImageUrl: String = "",
    val color: String = "#8B1A2D" // цвет сектора на колесе
)