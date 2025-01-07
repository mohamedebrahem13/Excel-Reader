package com.example.excel_reader.data.repository

import com.example.excel_reader.data.models.Item
import com.example.excel_reader.data.parser.ExcelParser
import com.example.excel_reader.domain.IExcelRepository
import java.io.InputStream
import javax.inject.Inject

class ExcelRepository @Inject constructor(private val excelParser: ExcelParser) :
    IExcelRepository {

    override suspend fun parseExcelFile(inputStream: InputStream): List<Item> {
        return excelParser.parseExcelFile(inputStream)
    }


}