package com.construction.expense.data.local

import androidx.room.TypeConverter
import com.construction.expense.domain.model.ExpenseStatus
import com.construction.expense.domain.model.PaymentMode
import com.construction.expense.domain.model.ProjectStatus

/**
 * Type converters for Room database
 */
class Converters {

    @TypeConverter
    fun fromProjectStatus(value: ProjectStatus): String {
        return value.name
    }

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus {
        return ProjectStatus.valueOf(value)
    }

    @TypeConverter
    fun fromExpenseStatus(value: ExpenseStatus): String {
        return value.name
    }

    @TypeConverter
    fun toExpenseStatus(value: String): ExpenseStatus {
        return ExpenseStatus.valueOf(value)
    }

    @TypeConverter
    fun fromPaymentMode(value: PaymentMode): String {
        return value.name
    }

    @TypeConverter
    fun toPaymentMode(value: String): PaymentMode {
        return PaymentMode.valueOf(value)
    }
}
