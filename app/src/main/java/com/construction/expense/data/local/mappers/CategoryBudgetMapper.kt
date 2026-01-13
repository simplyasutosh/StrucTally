package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.CategoryBudgetEntity
import com.construction.expense.domain.model.BudgetAlertLevel
import com.construction.expense.domain.model.CategoryBudget

/**
 * Extension functions to convert between CategoryBudgetEntity and CategoryBudget domain model.
 */

/**
 * Convert CategoryBudgetEntity to CategoryBudget domain model
 *
 * @param spentAmount Current spent amount (calculated from ExpenseDao)
 */
fun CategoryBudgetEntity.toDomain(spentAmount: Double = 0.0): CategoryBudget {
    val remainingAmount = budgetedAmount - spentAmount
    val utilizationPercent = if (budgetedAmount > 0) {
        ((spentAmount / budgetedAmount) * 100).toInt()
    } else {
        0
    }

    val alertLevel = when {
        utilizationPercent >= 100 -> BudgetAlertLevel.CRITICAL
        utilizationPercent >= 90 -> BudgetAlertLevel.HIGH
        utilizationPercent >= 75 -> BudgetAlertLevel.MEDIUM
        utilizationPercent >= 50 -> BudgetAlertLevel.LOW
        else -> BudgetAlertLevel.NONE
    }

    return CategoryBudget(
        id = id,
        projectId = projectId,
        categoryId = categoryId,
        budgetedAmount = budgetedAmount,
        alertThreshold50 = alertThreshold50,
        alertThreshold75 = alertThreshold75,
        alertThreshold90 = alertThreshold90,
        alertThreshold100 = alertThreshold100,
        customThreshold = customThreshold,
        modifiedDate = modifiedDate,
        // Computed properties
        spentAmount = spentAmount,
        remainingAmount = remainingAmount,
        utilizationPercent = utilizationPercent,
        alertLevel = alertLevel
    )
}

/**
 * Convert CategoryBudget domain model to CategoryBudgetEntity
 */
fun CategoryBudget.toEntity(): CategoryBudgetEntity {
    return CategoryBudgetEntity(
        id = id,
        projectId = projectId,
        categoryId = categoryId,
        budgetedAmount = budgetedAmount,
        alertThreshold50 = alertThreshold50,
        alertThreshold75 = alertThreshold75,
        alertThreshold90 = alertThreshold90,
        alertThreshold100 = alertThreshold100,
        customThreshold = customThreshold,
        modifiedDate = modifiedDate
    )
}

/**
 * Convert list of CategoryBudgetEntity to list of CategoryBudget
 *
 * @param spentAmountsMap Map of categoryId to spent amount
 */
fun List<CategoryBudgetEntity>.toDomainList(
    spentAmountsMap: Map<Int, Double> = emptyMap()
): List<CategoryBudget> {
    return map { entity ->
        val spentAmount = spentAmountsMap[entity.categoryId] ?: 0.0
        entity.toDomain(spentAmount)
    }
}
