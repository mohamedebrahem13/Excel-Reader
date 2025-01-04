package com.example.excel_reader.di

import com.example.excel_reader.data.parser.ExcelParser
import com.example.excel_reader.data.repository.ExcelRepository
import com.example.excel_reader.domain.IExcelRepository
import com.example.excel_reader.domain.usecase.ExcelUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
@Module
@InstallIn(ViewModelComponent::class)
object ExcelParserModule {

    @Provides
    fun provideExcelParser(): ExcelParser {
        return ExcelParser()
    }

    @Provides
    fun provideExcelRepository(excelParser: ExcelParser): IExcelRepository {
        return ExcelRepository(excelParser)
    }
    // Provides ExcelUseCase
    @Provides
    fun provideExcelUseCase(excelRepository: IExcelRepository): ExcelUseCase {
        return ExcelUseCase(excelRepository)
    }
}