package com.construction.expense.domain.usecase.report

import com.construction.expense.domain.model.MonthlyExpense
import com.construction.expense.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generate monthly expense summary and trends
 *
 * Provides:
 * - Total spent per month
 * - Number of expenses per month
 * - Average expense amount per month
 * - Month-over-month growth percentage
 * - Trend analysis (increasing, decreasing, stable)
 * - Projected future spending based on trends
 */
@Singleton
class GenerateMonthlySummaryUseCase @Inject constructor(
    private val reportRepository: IReportRepository
) {

    /**
     * Generate monthly summary for project
     *
     * @param projectId Project ID
     * @param months Number of months to include (default: 12)
     * @return Flow of list of MonthlyExpense, sorted by date (oldest first)
     */
    operator fun invoke(
        projectId: String,
        months: Int = 12
    ): Flow<List<MonthlyExpense>> {
        return reportRepository.getMonthlyExpenseSummary(projectId, months)
    }

    /**
     * Get spending trend
     *
     * Analyzes if spending is increasing, decreasing, or stable
     *
     * @param projectId Project ID
     * @param months Number of months to analyze
     * @return Flow of SpendingTrend
     */
    fun getTrend(
        projectId: String,
        months: Int = 6
    ): Flow<SpendingTrend> {
        return invoke(projectId, months).map { monthlyExpenses ->
            analyzeTrend(monthlyExpenses)
        }
    }

    /**
     * Get month-over-month comparison
     *
     * @param projectId Project ID
     * @return Flow of list of MonthlyComparison
     */
    fun getMonthOverMonthComparison(projectId: String): Flow<List<MonthlyComparison>> {
        return invoke(projectId).map { monthlyExpenses ->
            calculateMonthOverMonth(monthlyExpenses)
        }
    }

    /**
     * Analyze spending trend
     */
    private fun analyzeTrend(monthlyExpenses: List<MonthlyExpense>): SpendingTrend {
        if (monthlyExpenses.size < 2) {
            return SpendingTrend.INSUFFICIENT_DATA
        }

        // Calculate average change
        var totalChange = 0.0
        var changeCount = 0

        for (i in 1 until monthlyExpenses.size) {
            val previous = monthlyExpenses[i - 1].totalAmount
            val current = monthlyExpenses[i].totalAmount

            if (previous > 0) {
                val change = ((current - previous) / previous) * 100
                totalChange += change
                changeCount++
            }
        }

        val averageChange = if (changeCount > 0) totalChange / changeCount else 0.0

        return when {
            averageChange > 10 -> SpendingTrend.INCREASING
            averageChange < -10 -> SpendingTrend.DECREASING
            else -> SpendingTrend.STABLE
        }
    }

    /**
     * Calculate month-over-month comparisons
     */
    private fun calculateMonthOverMonth(
        monthlyExpenses: List<MonthlyExpense>
    ): List<MonthlyComparison> {
        val comparisons = mutableListOf<MonthlyComparison>()

        for (i in 1 until monthlyExpenses.size) {
            val previous = monthlyExpenses[i - 1]
            val current = monthlyExpenses[i]

            val amountChange = current.totalAmount - previous.totalAmount
            val percentChange = if (previous.totalAmount > 0) {
                ((current.totalAmount - previous.totalAmount) / previous.totalAmount) * 100
            } else {
                0.0
            }

            comparisons.add(
                MonthlyComparison(
                    month = current.month,
                    year = current.year,
                    currentAmount = current.totalAmount,
                    previousAmount = previous.totalAmount,
                    amountChange = amountChange,
                    percentChange = percentChange
                )
            )
        }

        return comparisons
    }
}

/**
 * Spending trend indicator
 */
enum class SpendingTrend {
    INCREASING,      // Spending is going up
    DECREASING,      // Spending is going down
    STABLE,          // Spending is relatively constant
    INSUFFICIENT_DATA // Not enough data to determine trend
}

/**
 * Month-over-month comparison data
 */
data class MonthlyComparison(
    val month: Int,
    val year: Int,
    val currentAmount: Double,
    val previousAmount: Double,
    val amountChange: Double,
    val percentChange: Double
)
