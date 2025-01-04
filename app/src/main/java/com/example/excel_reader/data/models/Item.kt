package com.example.excel_reader.data.models

data class Item(
    val productId: String,
    val productName: String,
    val unit: String,
    val cost: Double,
    val sellingPrice: Double
)