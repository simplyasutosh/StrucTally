package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.CategoryBudgetEntity
import com.construction.expense.domain.model.BudgetAlertLevel
import com.construction.expense.domain.model.CategoryBudget
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between CategoryBudgetEntity and CategoryBudget domain model.
 */
@Singleton
class CategoryBudgetMapper @Inject constructor() {
    
    /**
     * Convert CategoryBudgetEntity to CategoryBudget domain model
     *
     * @param entity Entity to convert
     * @param spentAmount Current spent amount (calculated from ExpenseDao)
     */
    fun toDomain(entity: CategoryBudgetEntity, spentAmount: Double = 0.0): CategoryBudget {
        val remainingAmount = entity.budgetedAmount - spentAmount
        val utilizationPercent = if (entity.budgetedAmount > 0) {
            ((spentAmount / entity.budgetedAmount) * 100).toInt()
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
            id = entity.id,
            projectId = entity.projectId,
            categoryId = entity.categoryId,
            budgetedAmount = entity.budgetedAmount,
            alertThreshold50 = entity.alertThreshold50,
            alertThreshold75 = entity.alertThreshold75,
            alertThreshold90 = entity.alertThreshold90,
            alertThreshold100 = entity.alertThreshold100,
            customThreshold = entity.customThreshold,
            modifiedDate = entity.modifiedDate,
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
    fun toEntity(budget: CategoryBudget): CategoryBudgetEntity {
        return CategoryBudgetEntity(
            id = budget.id,
            projectId = budget.projectId,
            categoryId = budget.categoryId,
            budgetedAmount = budget.budgetedAmount,
            alertThreshold50 = budget.alertThreshold50,
            alertThreshold75 = budget.alertThreshold75,
            alertThreshold90 = budget.alertThreshold90,
            alertThreshold100 = budget.alertThreshold100,
            customThreshold = budget.customThreshold,
            modifiedDate = budget.modifiedDate
        )
    }

    /**
     * Convert list of CategoryBudgetEntity to list of CategoryBudget
     *
     * @param entities List of entities to convert
     * @param spentAmountsMap Map of categoryId to spent amount
     */
    fun toDomainList(
        entities: List<CategoryBudgetEntity>,
        spentAmountsMap: Map<Int, Double> = emptyMap()
    ): List<CategoryBudget> {
        return entities.map { entity ->
            val spentAmount = spentAmountsMap[entity.categoryId] ?: 0.0
            toDomain(entity, spentAmount)
        }
    }
}
