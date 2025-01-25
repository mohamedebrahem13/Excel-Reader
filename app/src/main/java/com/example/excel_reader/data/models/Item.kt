package com.example.excel_reader.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val productId: String,
    val productName: String,
    val unit: String,
    val cost: Double,
    val sellingPrice: Double
): Parcelable

