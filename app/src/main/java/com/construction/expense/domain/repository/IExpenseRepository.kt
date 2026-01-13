package com.construction.expense.domain.repository

import com.construction.expense.domain.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Expense operations
 */
interface IExpenseRepository {

    /**
     * Create new expense
     */
    suspend fun createExpense(expense: Expense): Result<String>

    /**
     * Update existing expense
     */
    suspend fun updateExpense(expense: Expense): Result<Unit>

    /**
     * Delete expense permanently
     */
    suspend fun deleteExpense(expenseId: String): Result<Unit>

    /**
     * Soft delete expense (mark as deleted)
     */
    suspend fun softDeleteExpense(expenseId: String): Result<Unit>

    /**
     * Restore soft-deleted expense
     */
    suspend fun restoreExpense(expenseId: String): Result<Unit>

    /**
     * Get expense by ID
     */
    fun getExpenseById(expenseId: String): Flow<Expense?>

    /**
     * Get all expenses for a project
     */
    fun getExpensesByProject(projectId: String): Flow<List<Expense>>

    /**
     * Get recent expenses with limit
     */
    fun getRecentExpenses(projectId: String, limit: Int): Flow<List<Expense>>

    /**
     * Get expenses by category
     */
    fun getExpensesByCategory(projectId: String, categoryId: Int): Flow<List<Expense>>

    /**
     * Get expenses by subcategory
     */
    fun getExpensesBySubCategory(projectId: String, subCategoryId: String): Flow<List<Expense>>

    /**
     * Get expenses by room
     */
    fun getExpensesByRoom(projectId: String, roomId: Int): Flow<List<Expense>>

    /**
     * Get expenses by date range
     */
    fun getExpensesByDateRange(
        projectId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Expense>>

    /**
     * Get expenses by vendor
     */
    fun getExpensesByVendor(projectId: String, vendorName: String): Flow<List<Expense>>

    /**
     * Search expenses by query
     */
    fun searchExpenses(projectId: String, query: String): Flow<List<Expense>>

    /**
     * Get total expenses by category
     */
    fun getTotalByCategory(projectId: String, categoryId: Int): Flow<Double>

    /**
     * Get unsynced expenses for cloud sync
     */
    fun getUnsyncedExpenses(): Flow<List<Expense>>

    /**
     * Mark expenses as synced
     */
    suspend fun markAsSynced(expenseIds: List<String>): Result<Unit>
}
