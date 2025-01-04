package com.example.excel_reader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.excel_reader.data.models.Item
import com.example.excel_reader.domain.usecase.ExcelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ExcelViewModel @Inject constructor(
    private val excelUseCase: ExcelUseCase
) : ViewModel() {

    // MutableStateFlow to hold the current state
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    // Public StateFlow that can be observed from the UI
    val items: StateFlow<List<Item>> = _items

    // MutableStateFlow to track loading state for parsing
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // MutableStateFlow to track loading state for modifying the file
    private val _modifyLoading = MutableStateFlow(false)
    val modifyLoading: StateFlow<Boolean> = _modifyLoading

    // Parse Excel file and update the state with a list of items
    fun parseExcelFile(inputStream: InputStream) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val itemsList = excelUseCase.parseExcelFile(inputStream)
                _items.value = itemsList // Update state
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            } finally {
                _loading.value = false
            }
        }
    }

}