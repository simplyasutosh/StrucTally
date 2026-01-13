package com.construction.expense.domain.model

data class CategoryBudget(
    val id: Int,
    val projectId: String,
    val categoryId: Int,
    val budgetedAmount: Double,
    val alertThreshold50: Boolean,
    val alertThreshold75: Boolean,
    val alertThreshold90: Boolean,
    val alertThreshold100: Boolean,
    val customThreshold: Int? = null,
    val modifiedDate: Long,

    // Computed properties
    val spentAmount: Double = 0.0,
    val remainingAmount: Double = budgetedAmount - spentAmount,
    val utilizationPercent: Int = if (budgetedAmount > 0)
        ((spentAmount / budgetedAmount) * 100).toInt() else 0,
    val alertLevel: BudgetAlertLevel = calculateAlertLevel(utilizationPercent)
) {
    companion object {
        fun calculateAlertLevel(percent: Int): BudgetAlertLevel {
            return when {
                percent >= 100 -> BudgetAlertLevel.CRITICAL
                percent >= 90 -> BudgetAlertLevel.HIGH
                percent >= 75 -> BudgetAlertLevel.MEDIUM
                percent >= 50 -> BudgetAlertLevel.LOW
                else -> BudgetAlertLevel.NONE
            }
        }
    }
}

enum class BudgetAlertLevel {
    NONE, LOW, MEDIUM, HIGH, CRITICAL
}
