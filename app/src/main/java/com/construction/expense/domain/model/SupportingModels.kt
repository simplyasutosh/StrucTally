package com.construction.expense.domain.model

/**
 * Category with its subcategories (for dropdowns)
 */
data class CategoryWithSubCategories(
    val category: Category,
    val subCategories: List<SubCategory>
)

/**
 * Project with summary statistics
 */
data class ProjectWithSummary(
    val project: Project,
    val totalExpenses: Double,
    val expenseCount: Int,
    val budgetUtilization: Double,
    val thisMonthExpenses: Double,
    val lastExpenseDate: Long?
)

/**
 * Expense summary for reports
 */
data class ExpenseSummary(
    val totalAmount: Double,
    val expenseCount: Int,
    val averageAmount: Double,
    val categoryBreakdown: Map<String, Double>,
    val vendorBreakdown: Map<String, Double>,
    val monthlyTrend: List<MonthlyExpense>
)

/**
 * Monthly expense data for charts
 */
data class MonthlyExpense(
    val month: String, // "Jan 2025"
    val year: Int,
    val monthNumber: Int,
    val amount: Double,
    val expenseCount: Int
)

/**
 * Room expense summary
 */
data class RoomExpenseSummary(
    val roomId: Int,
    val roomName: String,
    val totalAmount: Double,
    val categoryBreakdown: Map<String, Double>,
    val expenseCount: Int,
    val costPerSqFt: Double?
)

/**
 * Category expense summary
 */
data class CategoryExpenseSummary(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val budgetedAmount: Double?,
    val percentOfTotal: Double,
    val expenseCount: Int,
    val subCategoryBreakdown: Map<String, Double>,
    val budgetStatus: BudgetStatus
)

enum class BudgetStatus {
    UNDER_BUDGET,
    AT_BUDGET,
    OVER_BUDGET
}

/**
 * Receipt data from OCR
 */
data class ReceiptData(
    val rawText: String,
    val amount: Double?,
    val date: Long?,
    val vendorName: String?,
    val invoiceNumber: String?,
    val items: List<LineItem>?,
    val gstNumber: String?,
    val gstAmount: Double?,
    val confidence: Float
)

data class LineItem(
    val description: String,
    val quantity: Int?,
    val price: Double?
)
