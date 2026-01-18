package com.construction.expense.domain.model

/**
 * Project expense summary
 */
data class ExpenseSummary(
    val project: Project,
    val totalExpenses: Double,
    val budgetUtilization: Double,
    val expenseCount: Int,
    val thisMonthExpenses: Double,
    val lastMonthExpenses: Double,
    val averageExpense: Double,
    val largestExpense: Expense? = null
)

/**
 * Category expense summary
 */
data class CategoryExpenseSummary(
    val categoryId: Int,
    val categoryName: String,
    val totalSpent: Double,
    val budgetedAmount: Double,
    val budgetUtilization: Double,
    val expenseCount: Int,
    val averageAmount: Double,
    val subcategoryBreakdown: List<SubCategoryExpense> = emptyList()
)

/**
 * Subcategory expense within a category
 */
data class SubCategoryExpense(
    val subCategoryId: String,
    val subCategoryName: String,
    val totalSpent: Double,
    val expenseCount: Int
)

/**
 * Room expense summary
 */
data class RoomExpenseSummary(
    val roomId: Int,
    val roomName: String,
    val totalSpent: Double,
    val expenseCount: Int,
    val averageAmount: Double,
    val categoryBreakdown: List<CategoryInRoom> = emptyList(),
    val mostExpensiveExpense: Expense? = null
)

/**
 * Category spending within a room
 */
data class CategoryInRoom(
    val categoryId: Int,
    val categoryName: String,
    val totalSpent: Double,
    val expenseCount: Int
)

/**
 * Monthly expense data
 */
data class MonthlyExpense(
    val month: Int,
    val year: Int,
    val totalAmount: Double,
    val expenseCount: Int,
    val averageAmount: Double
)

/**
 * Vendor expense summary
 */
data class VendorExpenseSummary(
    val vendorName: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val averageAmount: Double,
    val categories: List<String> = emptyList(),
    val preferredPaymentMode: String? = null,
    val lastTransactionDate: Long? = null
)

/**
 * Vendor diversity analysis
 */
data class VendorDiversityAnalysis(
    val totalVendors: Int,
    val totalAmount: Double,
    val top5Percentage: Double,
    val top10Percentage: Double,
    val concentrationLevel: ConcentrationLevel
)

/**
 * Concentration level for vendor diversity
 */
enum class ConcentrationLevel {
    HIGH,    // Risky - concentrated spending
    MEDIUM,  // Moderate concentration
    LOW      // Healthy - distributed spending
}


/**
 * Project with summary data
 */
data class ProjectWithSummary(
    val project: Project,
    val totalExpenses: Double,
    val budgetUtilization: Double,
    val expenseCount: Int,
    val thisMonthExpenses: Double
)

/**
 * Category with its subcategories
 */
data class CategoryWithSubCategories(
    val category: Category,
    val subCategories: List<SubCategory>
)
