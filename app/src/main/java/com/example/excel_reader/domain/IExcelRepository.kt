package com.example.excel_reader.domain

import com.example.excel_reader.data.models.Item
import java.io.InputStream

interface IExcelRepository {
    suspend fun parseExcelFile(inputStream: InputStream): List<Item>
}