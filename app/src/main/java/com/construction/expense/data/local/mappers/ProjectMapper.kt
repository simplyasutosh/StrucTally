package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.ProjectEntity
import com.construction.expense.domain.model.Project
import com.construction.expense.domain.model.ProjectStatus
import com.construction.expense.domain.model.ProjectType

/**
 * Extension functions to convert between ProjectEntity and Project domain model.
 *
 * Mappers handle:
 * - Enum conversion (String ↔ Enum)
 * - Adding computed properties (totalExpenses, expenseCount)
 * - Bidirectional conversion
 */

/**
 * Convert ProjectEntity to Project domain model
 *
 * @param totalExpenses Total expenses calculated from ExpenseDao
 * @param expenseCount Number of expenses calculated from ExpenseDao
 */
fun ProjectEntity.toDomain(
    totalExpenses: Double = 0.0,
    expenseCount: Int = 0
): Project {
    return Project(
        id = id,
        name = name,
        type = ProjectType.valueOf(type),
        location = location,
        startDate = startDate,
        expectedEndDate = expectedEndDate,
        actualEndDate = actualEndDate,
        status = ProjectStatus.valueOf(status),
        landCost = landCost,
        totalBudget = totalBudget,
        loanAmount = loanAmount,
        bankName = bankName,
        interestRate = interestRate,
        emiAmount = emiAmount,
        ownerName = ownerName,
        coOwnerName = coOwnerName,
        contractorName = contractorName,
        contractorContact = contractorContact,
        architectName = architectName,
        architectContact = architectContact,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        // Computed properties
        totalExpenses = totalExpenses,
        expenseCount = expenseCount,
        budgetRemaining = totalBudget - totalExpenses,
        budgetUtilization = if (totalBudget > 0) (totalExpenses / totalBudget * 100) else 0.0,
        isOverBudget = totalExpenses > totalBudget,
        durationInDays = ((actualEndDate ?: System.currentTimeMillis()) - startDate) / (1000 * 60 * 60 * 24)
    )
}

/**
 * Convert Project domain model to ProjectEntity
 */
fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        type = type.name,
        location = location,
        startDate = startDate,
        expectedEndDate = expectedEndDate,
        actualEndDate = actualEndDate,
        status = status.name,
        landCost = landCost,
        totalBudget = totalBudget,
        loanAmount = loanAmount,
        bankName = bankName,
        interestRate = interestRate,
        emiAmount = emiAmount,
        ownerName = ownerName,
        coOwnerName = coOwnerName,
        contractorName = contractorName,
        contractorContact = contractorContact,
        architectName = architectName,
        architectContact = architectContact,
        createdDate = createdDate,
        modifiedDate = modifiedDate
    )
}

/**
 * Convert list of ProjectEntity to list of Project
 */
fun List<ProjectEntity>.toDomainList(
    expensesMap: Map<String, Pair<Double, Int>> = emptyMap()
): List<Project> {
    return map { entity ->
        val (totalExpenses, expenseCount) = expensesMap[entity.id] ?: (0.0 to 0)
        entity.toDomain(totalExpenses, expenseCount)
    }
}
