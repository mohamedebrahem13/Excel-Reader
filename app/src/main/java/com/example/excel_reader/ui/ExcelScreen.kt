package com.example.excel_reader.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExcelScreen(modifier: Modifier = Modifier, excelViewModel: ExcelViewModel = hiltViewModel()) {
    val items by excelViewModel.items.collectAsState() // Collect the list of items
    val loading by excelViewModel.loading.collectAsState() // Collect the loading state for parsing
    val modifyLoading by excelViewModel.modifyLoading.collectAsState() // Collect loading state for modifying
    val context = LocalContext.current

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // File picker launcher for picking an Excel file
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                inputStream?.let { stream ->
                    excelViewModel.parseExcelFile(stream)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to open file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Filter items based on the search query
    val filteredItems = if (searchQuery.isEmpty()) {
        items // Show all items if no search query
    } else {
        items.filter { it.productId.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Product ID") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Button to pick an Excel file
        Button(
            onClick = { pickFileLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
            enabled = !loading && !modifyLoading // Disable if loading or modifying
        ) {
            Text(text = "Pick an Excel File")
        }

        // Show loading indicator when loading is true (for parsing)
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        // Display the list of items with header row
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            item {
                // Header row
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(modifier = Modifier.weight(1f), text = "Product ID", style = MaterialTheme.typography.bodyMedium)
                    Text(modifier = Modifier.weight(1f), text = "Product Name", style = MaterialTheme.typography.bodyMedium)
                    Text(modifier = Modifier.weight(1f), text = "Unit", style = MaterialTheme.typography.bodyMedium)
                    Text(modifier = Modifier.weight(1f), text = "Cost", style = MaterialTheme.typography.bodyMedium)
                    Text(modifier = Modifier.weight(1f), text = "Selling Price", style = MaterialTheme.typography.bodyMedium)
                }
            }
            items(filteredItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp) // Dynamic padding
                        .background(
                            if (item.productId.contains(searchQuery, ignoreCase = true)) Color.Yellow
                            else Color.Transparent
                        )
                ) {
                    Text(modifier = Modifier.weight(1f), text = item.productId)
                    Text(modifier = Modifier.weight(1f), text = item.productName)
                    Text(modifier = Modifier.weight(1f), text = item.unit)
                    Text(modifier = Modifier.weight(1f), text = item.cost.toString())
                    Text(modifier = Modifier.weight(1f), text = item.sellingPrice.toString())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExcelScreen() {
    ExcelScreen()
}