package com.construction.expense.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.construction.expense.data.local.dao.*
import com.construction.expense.data.local.entities.*

/**
 * Room database for Construction Expense Tracker
 */
@Database(
    entities = [
        ProjectEntity::class,
        CategoryEntity::class,
        SubCategoryEntity::class,
        CategoryBudgetEntity::class,
        RoomEntity::class,
        MilestoneEntity::class,
        VendorEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ConstructionExpenseDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subCategoryDao(): SubCategoryDao
    abstract fun categoryBudgetDao(): CategoryBudgetDao
    abstract fun roomDao(): RoomDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun vendorDao(): VendorDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "construction_expense_db"
    }
}
