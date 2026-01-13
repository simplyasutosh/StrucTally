package com.construction.expense.domain.usecase.report

import com.construction.expense.domain.model.CategoryExpenseSummary
import com.construction.expense.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generate category-wise expense summary
 *
 * Provides for each category:
 * - Category name and ID
 * - Total spent
 * - Budgeted amount
 * - Budget utilization percentage
 * - Number of expenses
 * - Average expense amount
 * - Budget status (on track, warning, exceeded)
 * - Subcategory breakdown
 */
@Singleton
class GenerateCategorySummaryUseCase @Inject constructor(
    private val reportRepository: IReportRepository
) {

    /**
     * Generate category-wise summary for project
     *
     * @param projectId Project ID
     * @return Flow of list of CategoryExpenseSummary, sorted by total spent (descending)
     */
    operator fun invoke(projectId: String): Flow<List<CategoryExpenseSummary>> {
        return reportRepository.getCategoryExpenseSummary(projectId)
    }

    /**
     * Generate summary for specific category
     *
     * @param projectId Project ID
     * @param categoryId Category ID
     * @return Flow of CategoryExpenseSummary for the specific category
     */
    fun forCategory(projectId: String, categoryId: Int): Flow<CategoryExpenseSummary?> {
        return reportRepository.getCategoryExpenseSummaryById(projectId, categoryId)
    }
}
