package com.construction.expense.domain.usecase.report

import com.construction.expense.domain.model.ExpenseSummary
import com.construction.expense.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generate comprehensive project summary report
 *
 * Provides:
 * - Total expenses
 * - Total budget
 * - Budget utilization percentage
 * - Number of expenses
 * - Expense count by status
 * - Average expense amount
 * - Largest expense
 * - Recent activity
 */
@Singleton
class GenerateProjectSummaryUseCase @Inject constructor(
    private val reportRepository: IReportRepository
) {

    /**
     * Generate project summary for given project
     *
     * @param projectId Project ID
     * @return Flow of ExpenseSummary with real-time updates
     */
    operator fun invoke(projectId: String): Flow<ExpenseSummary> {
        return reportRepository.getExpenseSummary(projectId)
    }
}
