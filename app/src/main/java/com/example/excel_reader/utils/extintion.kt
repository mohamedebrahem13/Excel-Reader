package com.example.excel_reader.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.widget.Toast
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.excel_reader.data.models.Item

// Function to convert mm to pixels based on screen density
fun mmToPixels(context: Context, mm: Float): Float {
    val density = context.resources.displayMetrics.densityDpi
    val mmPerInch = 25.4f
    return mm * (density / mmPerInch)
}

// Function to generate a Bitmap from the items list, adjusting the height to fit content
fun generateImageFromItems(context: Context, items: List<Item>): Bitmap {
    // Convert 79.5 mm to pixels for the image width (receipt width)
    val imageWidthPx = mmToPixels(context, 79.5f).toInt()


    val paint = Paint()
    paint.color = Color.BLACK
    paint.textSize = 40f // Text size for the content

    val rect = Rect()
    var totalHeight = 0f // Start with 0 height to calculate dynamically

    // Loop through the items and calculate total height
    items.forEach { item ->
        // Add height for each line (Product ID, Product Name, etc.)
        paint.getTextBounds(item.productId, 0, item.productId.length, rect)
        totalHeight += rect.height() + 10f // Add space between lines

        paint.getTextBounds(item.productName, 0, item.productName.length, rect)
        totalHeight += rect.height() + 10f

        paint.getTextBounds(item.unit, 0, item.unit.length, rect)
        totalHeight += rect.height() + 10f

        paint.getTextBounds(item.cost.toString(), 0, item.cost.toString().length, rect)
        totalHeight += rect.height() + 10f

        paint.getTextBounds(item.sellingPrice.toString(), 0, item.sellingPrice.toString().length, rect)
        totalHeight += rect.height() + 20f // Add extra space before the next item
    }

    // Set a padding for the top and bottom of the receipt
    val topBottomPadding = 50f

    // Final height is the calculated content height + padding
    val imageHeightPx = (totalHeight + topBottomPadding * 2).toInt()

    // Create a bitmap with calculated size
    val bitmap = Bitmap.createBitmap(imageWidthPx, imageHeightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw background color (white)
    canvas.drawColor(Color.WHITE)

    // Start drawing text from top
    var yPos = topBottomPadding // Start Y position with padding
    items.forEach { item ->
        // Draw each item data in the format you want
        paint.getTextBounds(item.productId, 0, item.productId.length, rect)
        canvas.drawText("Product ID: ${item.productId}", 50f, yPos, paint)
        yPos += rect.height() + 10f

        paint.getTextBounds(item.productName, 0, item.productName.length, rect)
        canvas.drawText("Product Name: ${item.productName}", 50f, yPos, paint)
        yPos += rect.height() + 10f

        paint.getTextBounds(item.unit, 0, item.unit.length, rect)
        canvas.drawText("Unit: ${item.unit}", 50f, yPos, paint)
        yPos += rect.height() + 10f

        paint.getTextBounds(item.cost.toString(), 0, item.cost.toString().length, rect)
        canvas.drawText("Cost: ${item.cost}", 50f, yPos, paint)
        yPos += rect.height() + 10f

        paint.getTextBounds(item.sellingPrice.toString(), 0, item.sellingPrice.toString().length, rect)
        canvas.drawText("Selling Price: ${item.sellingPrice}", 50f, yPos, paint)
        yPos += rect.height() + 20f // Add extra space before next item
    }

    // Return the generated Bitmap for the receipt preview
    return bitmap
}
 fun printBitmap(context: Context, bitmap: Bitmap) {
    try {
        // Select the first paired Bluetooth printer
        val connection = BluetoothPrintersConnections.selectFirstPaired()
        if (connection == null) {
            Toast.makeText(context, "No paired Bluetooth printer found.", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize the printer
        val printer = EscPosPrinter(connection, 203, 48f, 32)

        // Convert the Bitmap to a hexadecimal string for the printer
        val hexString = PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmap)

        // Print the Bitmap
        printer.printFormattedText("[C]$hexString\n")

        // Show success message
        Toast.makeText(context, "Bitmap printed successfully.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Printing failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

