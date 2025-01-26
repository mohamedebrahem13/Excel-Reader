package com.example.excel_reader.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.excel_reader.data.models.Item
import com.example.excel_reader.utils.generateImageFromItems
import com.example.excel_reader.utils.printBitmap
import kotlinx.coroutines.launch

@Composable
fun PrintItemsScreen(modifier: Modifier = Modifier) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var itemList by remember { mutableStateOf(listOf<Item>()) }

    // Shared state for preview and printing
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isPreviewDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = itemPrice,
            onValueChange = { itemPrice = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (itemName.isNotBlank() && itemPrice.isNotBlank()) {
                    val price = itemPrice.toDoubleOrNull()
                    if (price != null && price > 0) {
                        val newItem = Item(
                            productId = (itemList.size + 1).toString(),
                            productName = itemName,
                            unit = "1 unit",
                            cost = price * 0.8,
                            sellingPrice = price
                        )
                        itemList = itemList + newItem
                    } else {
                        Toast.makeText(
                            context,
                            "Price must be greater than zero",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(itemList.size) { index ->
                val item = itemList[index]
                Text(
                    text = "${item.productName} - $${item.sellingPrice}",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preview Button
        Button(
            onClick = {
                if (itemList.isNotEmpty()) {
                    coroutineScope.launch {
                        previewBitmap =
                            generateImageFromItems(itemList) // Generate bitmap for preview
                        isPreviewDialogOpen = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Preview")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Print Button
        Button(
            onClick = {
                if (itemList.isNotEmpty()) {
                    coroutineScope.launch {
                        isButtonEnabled = false
                        previewBitmap =
                            generateImageFromItems(itemList) // Generate bitmap for print
                        printBitmap(context, previewBitmap!!)
                        isButtonEnabled = true
                    }
                }
            },
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isButtonEnabled) "Print" else "Loading")
        }
    }
// Preview Dialog
    if (isPreviewDialogOpen) {
        AlertDialog(
            onDismissRequest = { isPreviewDialogOpen = false },
            title = { Text("Preview") },
            text = {
                previewBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Preview Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                } ?: Text("No image available", modifier = Modifier.padding(8.dp))
            },
            confirmButton = {
                Button(onClick = { isPreviewDialogOpen = false }) {
                    Text("Close")
                }
            }
        )
    }
}