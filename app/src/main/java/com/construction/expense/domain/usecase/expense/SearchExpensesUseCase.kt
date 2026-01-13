package com.construction.expense.domain.usecase.expense

import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for searching expenses.
 *
 * Supports:
 * - Text search in vendor, category, notes
 * - Amount search
 * - Fuzzy matching
 */
@Singleton
class SearchExpensesUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {

    /**
     * Search expenses by query
     *
     * @param projectId Project ID
     * @param query Search query (can be vendor name, amount, category, etc.)
     * @return Flow of matching expenses
     */
    operator fun invoke(projectId: String, query: String): Flow<List<Expense>> {
        if (query.isBlank()) {
            return expenseRepository.getExpensesByProject(projectId)
        }

        return expenseRepository.searchExpenses(projectId, query.trim())
            .map { expenses ->
                // Sort results by relevance
                expenses.sortedByDescending { expense ->
                    calculateRelevanceScore(expense, query)
                }
            }
    }

    /**
     * Calculate relevance score for search results
     *
     * Higher score = more relevant
     */
    private fun calculateRelevanceScore(expense: Expense, query: String): Int {
        var score = 0
        val lowerQuery = query.lowercase()

        // Exact match in vendor name: +10
        if (expense.vendorName.lowercase() == lowerQuery) {
            score += 10
        }
        // Contains in vendor name: +5
        else if (expense.vendorName.lowercase().contains(lowerQuery)) {
            score += 5
        }

        // Exact match in category: +8
        if (expense.categoryName.lowercase() == lowerQuery) {
            score += 8
        }
        // Contains in category: +4
        else if (expense.categoryName.lowercase().contains(lowerQuery)) {
            score += 4
        }

        // Match in notes: +3
        if (expense.notes?.lowercase()?.contains(lowerQuery) == true) {
            score += 3
        }

        // Match in amount (as string): +2
        if (expense.amount.toString().contains(query)) {
            score += 2
        }

        return score
    }
}
