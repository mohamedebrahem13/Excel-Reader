package com.example.excel_reader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.excel_reader.ui.ExcelScreen
import com.example.excel_reader.ui.theme.ExcelReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // For edge-to-edge content (if supported)
        setContent {
            ExcelReaderTheme {
                Scaffold(
                    content = { paddingValues ->
                        // Pass paddingValues to the ExcelScreen to ensure content is padded correctly
                        ExcelScreen(modifier = Modifier.padding(paddingValues))
                    }
                )
            }
        }
    }
}
