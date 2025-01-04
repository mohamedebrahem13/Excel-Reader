package com.example.excel_reader.data.parser

import com.example.excel_reader.data.models.Item
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

class ExcelParser {

    // Parse the Excel file and return a list of items
    fun parseExcelFile(inputStream: InputStream): List<Item> {
        val itemsList = mutableListOf<Item>()

        try {
            // Create a workbook from the input stream
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)  // Get the first sheet

            // Iterate through each row in the sheet (starting from row 1 to skip the header)
            for (row in sheet) {
                if (row.rowNum == 0) continue // Skip header row

                // Safely read cell values, checking if the cell is null
                val productId = row.getCell(0)?.toString() ?: ""
                val productName = row.getCell(1)?.toString() ?: ""
                val unit = row.getCell(2)?.toString() ?: ""
                val cost = row.getCell(3)?.toString()?.toDoubleOrNull() ?: 0.0
                val sellingPrice = row.getCell(4)?.toString()?.toDoubleOrNull() ?: 0.0

                // Create an Item and add it to the list
                val item = Item(productId, productName, unit, cost, sellingPrice)
                itemsList.add(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Handle exception, you might log this to analytics or a file
        }

        return itemsList
    }

    // Modify the Excel file and save it to the output stream
    fun modifyExcelFile(inputStream: InputStream, outputStream: OutputStream, items: List<Item>) {
        try {
            val workbook: Workbook = XSSFWorkbook(inputStream)
            val sheet: Sheet = workbook.getSheetAt(0) // Assuming data is in the first sheet

            // Iterate over the rows and update the sheet with the modified data
            for ((index, item) in items.withIndex()) {
                val row: Row = sheet.getRow(index + 1)  // Skip the header row (index + 1)

                // Update each cell with the new value
                row.getCell(0)?.setCellValue(item.productId) // Update productId
                row.getCell(1)?.setCellValue(item.productName) // Update productName
                row.getCell(2)?.setCellValue(item.unit) // Update unit
                row.getCell(3)?.setCellValue(item.cost) // Update cost
                row.getCell(4)?.setCellValue(item.sellingPrice) // Update selling price
            }

            // Write the updated workbook to the output stream
            workbook.write(outputStream)
            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()  // Handle exception
        }
    }
}