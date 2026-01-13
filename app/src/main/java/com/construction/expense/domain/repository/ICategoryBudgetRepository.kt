package com.construction.expense.domain.repository

import com.construction.expense.domain.model.CategoryBudget
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Category Budget operations
 */
interface ICategoryBudgetRepository {

    /**
     * Create or update budget for category
     */
    suspend fun setBudget(budget: CategoryBudget): Result<Unit>

    /**
     * Get budget for category
     */
    suspend fun getBudget(projectId: String, categoryId: Int): CategoryBudget?

    /**
     * Get all budgets for project
     */
    fun getAllBudgets(projectId: String): Flow<List<CategoryBudget>>

    /**
     * Delete budget
     */
    suspend fun deleteBudget(projectId: String, categoryId: Int): Result<Unit>
}
