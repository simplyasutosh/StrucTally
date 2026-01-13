package com.construction.expense.domain.repository

import com.construction.expense.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Report operations
 */
interface IReportRepository {

    /**
     * Get expense summary for project
     */
    fun getExpenseSummary(projectId: String): Flow<ExpenseSummary>

    /**
     * Get category-wise expense summary
     */
    fun getCategoryExpenseSummary(projectId: String): Flow<List<CategoryExpenseSummary>>

    /**
     * Get category expense summary by ID
     */
    fun getCategoryExpenseSummaryById(projectId: String, categoryId: Int): Flow<CategoryExpenseSummary?>

    /**
     * Get room-wise expense summary
     */
    fun getRoomExpenseSummary(projectId: String): Flow<List<RoomExpenseSummary>>

    /**
     * Get room expense summary by ID
     */
    fun getRoomExpenseSummaryById(projectId: String, roomId: Int): Flow<RoomExpenseSummary?>

    /**
     * Get monthly expense summary
     */
    fun getMonthlyExpenseSummary(projectId: String, months: Int): Flow<List<MonthlyExpense>>

    /**
     * Get vendor expense summary
     */
    fun getVendorExpenseSummary(projectId: String): Flow<List<VendorExpenseSummary>>

    /**
     * Get vendor expense summary by name
     */
    fun getVendorExpenseSummaryByName(projectId: String, vendorName: String): Flow<VendorExpenseSummary?>
}
