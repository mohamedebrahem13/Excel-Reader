package com.example.excel_reader.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
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
import kotlin.random.Random

fun generateImageFromItems(items: List<Item>): Bitmap {
    val imageWidthPx = (201 * 70) / 25.4f // Approximate 554 pixels

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
        textAlign = Paint.Align.LEFT
    }

    val rect = Rect()
    var totalHeight = 0f // To dynamically calculate content height

    // Calculate the total height for the content
    items.forEach { item ->
        paint.getTextBounds(item.productName, 0, item.productName.length, rect)
        totalHeight += rect.height() + 10f

        paint.getTextBounds(item.sellingPrice.toString(), 0, item.sellingPrice.toString().length, rect)
        totalHeight += rect.height() + 20f
    }

    val headerFooterHeight = 200f
    val topBottomPadding = 50f
    val imageHeightPx = (totalHeight + topBottomPadding * 2 + headerFooterHeight * 2).toInt()

    // Create bitmap
    val bitmap = Bitmap.createBitmap(imageWidthPx.toInt(), imageHeightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)

    // Draw today's date
    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
    val xCenter = imageWidthPx / 2f
    val yDatePosition = headerFooterHeight / 4f

    canvas.drawText(
        currentDate,
        xCenter - paint.measureText(currentDate) / 2,
        yDatePosition,
        paint
    )

    val yHeaderStart = yDatePosition + paint.textSize + 10f

    // Draw header and invoice number (Arabic text)
    val headerLine1 = "بكير"
    val headerLine2 = "مبيعات"
    val invoiceNumber = "رقم الفاتورة: ${"INV-${Random.nextInt(1000, 9999)}"}"

    canvas.drawText(
        headerLine1,
        xCenter - paint.measureText(headerLine1) / 2,
        yHeaderStart,
        paint
    )

    canvas.drawText(
        headerLine2,
        xCenter - paint.measureText(headerLine2) / 2,
        yHeaderStart + paint.textSize + 10f,
        paint
    )

    canvas.drawText(
        invoiceNumber,
        xCenter - paint.measureText(invoiceNumber) / 2,
        yHeaderStart + paint.textSize * 2 + 20f,
        paint
    )

    val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    val lineY = yHeaderStart + paint.textSize * 3 + 30f
    canvas.drawLine(0f, lineY, imageWidthPx, lineY, linePaint)

    var yPos = lineY + 30f
    val lineSpacing = 20f // Increase the space between each line of product

    // Iterate through items to draw product names and prices
    items.forEach { item ->
        val arabicProductName = "اسم المنتج: ${item.productName}"
        val arabicSellingPrice = "السعر: ${item.sellingPrice}"

        val productNameWidth = paint.measureText(arabicProductName)
        val priceWidth = paint.measureText(arabicSellingPrice)

        if (productNameWidth + priceWidth > imageWidthPx - 20f) {
            // Move price to the next line if it doesn't fit
            val truncatedProductName = arabicProductName
            val productNameXPos = 10f

            // Draw product name
            canvas.drawText(truncatedProductName, productNameXPos, yPos, paint)

            // Move yPos to the next line after drawing the price
            yPos += rect.height() // Only move down by the height of the product name

            // Draw the price below the product name
            val sellingPriceXPos = 10f
            canvas.drawText(arabicSellingPrice, sellingPriceXPos, yPos, paint)
        } else {
            // If the product name and price fit on the same line
            val productNameXPos = imageWidthPx - productNameWidth - 10f
            val sellingPriceXPos = 10f

            // Draw product name
            canvas.drawText(arabicProductName, productNameXPos, yPos, paint)

            // Draw selling price
            canvas.drawText(arabicSellingPrice, sellingPriceXPos, yPos, paint)
        }

        // Adjust for the next item by adding space after each line
        yPos += rect.height() + lineSpacing // Increase the space between each line
    }
    // Draw dashed line and footer text
    val lineAboveFooterY = imageHeightPx - topBottomPadding - 5f
    canvas.drawLine(0f, lineAboveFooterY, imageWidthPx, lineAboveFooterY, linePaint)


   // Move the footer text down by increasing the Y position
    val footerYPosition = imageHeightPx - topBottomPadding + 20f  // Adjust the value to move down
    val footerText = "شكرا لك على زيارتك"

    canvas.drawText(
        footerText,
        (imageWidthPx - paint.measureText(footerText)) / 2,
        footerYPosition,
        paint
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
        val printer = EscPosPrinter(connection, 203, 70f, 48)

        // Get the bitmap's width and height
        val width = bitmap.width
        val height = bitmap.height

        // Calculate the new width and height based on the printer's width
        val targetWidthPx = (203 * 70) / 25.4f // This is the target width in pixels (calculated earlier)
        val scaleFactor = targetWidthPx / width.toFloat()

        // Resize the bitmap if it's smaller than the target width
        val resizedBitmap = if (width < targetWidthPx) {
            Bitmap.createScaledBitmap(bitmap, targetWidthPx.toInt(), (height * scaleFactor).toInt(), true)
        } else {
            bitmap
        }

        // Get the new width and height of the resized bitmap
        val resizedWidth = resizedBitmap.width
        val resizedHeight = resizedBitmap.height

        // StringBuilder to store all chunks of the print job
        val textToPrint = StringBuilder()

        // Split the image into chunks and convert each chunk to hexadecimal string for the printer
        for (y in 0 until resizedHeight step 256) {
            // Create a smaller bitmap (chunk) for each 256px slice
            val chunkBitmap = Bitmap.createBitmap(
                resizedBitmap,
                0,
                y,
                resizedWidth,
                if (y + 256 >= resizedHeight) resizedHeight - y else 256
            )

            // Convert the chunk bitmap to a hexadecimal string
            val hexString = PrinterTextParserImg.bitmapToHexadecimalString(printer, chunkBitmap)

            // Add the chunk to the text to print
            textToPrint.append("[C]<img>$hexString</img>\n")
        }

        // Send the formatted text to the printer
        printer.printFormattedText(textToPrint.toString())

        // Show success message
        Toast.makeText(context, "Bitmap printed successfully.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Printing failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
