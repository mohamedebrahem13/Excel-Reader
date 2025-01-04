package com.example.excel_reader.domain.usecase

import com.example.excel_reader.data.models.Item
import com.example.excel_reader.domain.IExcelRepository
import java.io.InputStream
import javax.inject.Inject

class ExcelUseCase @Inject constructor(private val excelRepository: IExcelRepository) {

    // Parse Excel file and return a list of items
    suspend fun parseExcelFile(inputStream: InputStream): List<Item> {
        return excelRepository.parseExcelFile(inputStream)
    }

}