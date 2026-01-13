package com.construction.expense.domain.usecase.expense

import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.model.PaymentMode
import com.construction.expense.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting expenses with complex filters.
 *
 * Supports filtering by:
 * - Category
 * - Subcategory
 * - Room
 * - Date range
 * - Amount range
 * - Payment mode
 * - Vendor
 * - Milestone
 */
@Singleton
class GetExpensesByFilterUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {

    operator fun invoke(
        projectId: String,
        filter: ExpenseFilter
    ): Flow<List<Expense>> {

        // Start with all expenses
        var expensesFlow = expenseRepository.getExpensesByProject(projectId)

        // Apply category filter
        filter.categoryId?.let { categoryId ->
            expensesFlow = expenseRepository.getExpensesByCategory(projectId, categoryId)
        }

        // Apply subcategory filter
        filter.subCategoryId?.let { subCategoryId ->
            expensesFlow = expenseRepository.getExpensesBySubCategory(projectId, subCategoryId)
        }

        // Apply room filter
        filter.roomId?.let { roomId ->
            expensesFlow = expenseRepository.getExpensesByRoom(projectId, roomId)
        }

        // Apply date range filter
        if (filter.startDate != null && filter.endDate != null) {
            expensesFlow = expenseRepository.getExpensesByDateRange(
                projectId,
                filter.startDate,
                filter.endDate
            )
        }

        // Apply vendor filter
        filter.vendorName?.let { vendorName ->
            expensesFlow = expenseRepository.getExpensesByVendor(projectId, vendorName)
        }

        // Apply additional filters in memory (amount range, payment mode)
        return expensesFlow.map { expenses ->
            expenses.filter { expense ->
                matchesFilter(expense, filter)
            }
        }
    }

    /**
     * Check if expense matches all filter criteria
     */
    private fun matchesFilter(expense: Expense, filter: ExpenseFilter): Boolean {
        // Amount range filter
        if (filter.minAmount != null && expense.amount < filter.minAmount) {
            return false
        }
        if (filter.maxAmount != null && expense.amount > filter.maxAmount) {
            return false
        }

        // Payment mode filter
        if (filter.paymentMode != null && expense.paymentMode != filter.paymentMode) {
            return false
        }

        // Milestone filter
        if (filter.milestoneId != null && expense.milestoneId != filter.milestoneId) {
            return false
        }

        return true
    }
}

/**
 * Expense filter criteria
 */
data class ExpenseFilter(
    val categoryId: Int? = null,
    val subCategoryId: String? = null,
    val roomId: Int? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val paymentMode: PaymentMode? = null,
    val vendorName: String? = null,
    val milestoneId: Int? = null
)
