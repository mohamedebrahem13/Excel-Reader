package com.example.excel_reader.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.widget.Toast
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.excel_reader.data.models.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Function to convert mm to pixels based on screen density
fun mmToPixels(context: Context, mm: Float): Float {
    val density = context.resources.displayMetrics.densityDpi
    val mmPerInch = 25.4f
    return mm * (density / mmPerInch)
}

fun generateImageFromItems(context: Context, items: List<Item>): Bitmap {
    // Convert 79.5 mm to pixels for the image width (receipt width)
    val imageWidthPx = mmToPixels(context, 79.5f).toInt()

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 40f // Text size for the content
    }
    val headerFooterPaint = Paint().apply {
        color = Color.BLACK
        textSize = 50f // Slightly larger for header/footer
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD) // Bold text
    }
    val datePaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f // Slightly smaller for the date
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    val rect = Rect()
    var totalHeight = 0f // Start with 0 height to calculate dynamically

    // Calculate the total height for the content
    items.forEach { item ->
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

    // Set padding for top, bottom, and header/footer
    val headerFooterHeight = 200f // Adjusted height for Arabic text + date
    val topBottomPadding = 50f
    val imageHeightPx = (totalHeight + topBottomPadding * 2 + headerFooterHeight * 2).toInt()

    // Create a bitmap with calculated size
    val bitmap = Bitmap.createBitmap(imageWidthPx, imageHeightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw background color (white)
    canvas.drawColor(Color.WHITE)

    // Draw today's date
    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
    val xCenter = imageWidthPx / 2f
    val yDatePosition = headerFooterHeight / 4f

    canvas.drawText(
        currentDate,
        xCenter - datePaint.measureText(currentDate) / 2,
        yDatePosition,
        datePaint
    )

    // Draw Arabic header text
    val headerLine1 = "بكير"
    val headerLine2 = "مبيعات"

    val yHeaderStart = yDatePosition + datePaint.textSize + 20f // Position header below date

    // Draw first line of the header (centered)
    canvas.drawText(
        headerLine1,
        xCenter - headerFooterPaint.measureText(headerLine1) / 2,
        yHeaderStart,
        headerFooterPaint
    )

    // Draw second line of the header (below the first line)
    canvas.drawText(
        headerLine2,
        xCenter - headerFooterPaint.measureText(headerLine2) / 2,
        yHeaderStart + headerFooterPaint.textSize + 10f, // Add spacing between lines
        headerFooterPaint
    )

    // Start drawing text content below the header
    var yPos = headerFooterHeight + topBottomPadding
    items.forEach { item ->
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
        yPos += rect.height() + 20f
    }

    // Draw footer text
    val footerText = "شكؤا لك على زيارتك"
    canvas.drawText(
        footerText,
        (imageWidthPx - headerFooterPaint.measureText(footerText)) / 2,
        imageHeightPx - topBottomPadding,
        headerFooterPaint
    )

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

