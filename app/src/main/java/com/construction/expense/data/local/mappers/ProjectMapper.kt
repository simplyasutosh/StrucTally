package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.ProjectEntity
import com.construction.expense.domain.model.Project
import com.construction.expense.domain.model.ProjectStatus
import com.construction.expense.domain.model.ProjectType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between ProjectEntity and Project domain model.
 *
 * Mappers handle:
 * - Enum conversion (String ↔ Enum)
 * - Adding computed properties (totalExpenses, expenseCount)
 * - Bidirectional conversion
 */
@Singleton
class ProjectMapper @Inject constructor() {

    /**
     * Convert ProjectEntity to Project domain model
     *
     * @param entity Entity to convert
     * @param totalExpenses Total expenses calculated from ExpenseDao
     * @param expenseCount Number of expenses calculated from ExpenseDao
     */
    fun toDomain(
        entity: ProjectEntity,
        totalExpenses: Double = 0.0,
        expenseCount: Int = 0
    ): Project {
        return Project(
            id = entity.id,
            name = entity.name,
            type = ProjectType.valueOf(entity.type),
            location = entity.location,
            startDate = entity.startDate,
            expectedEndDate = entity.expectedEndDate,
            actualEndDate = entity.actualEndDate,
            status = ProjectStatus.valueOf(entity.status),
            landCost = entity.landCost,
            totalBudget = entity.totalBudget,
            loanAmount = entity.loanAmount,
            bankName = entity.bankName,
            interestRate = entity.interestRate,
            emiAmount = entity.emiAmount,
            ownerName = entity.ownerName,
            coOwnerName = entity.coOwnerName,
            contractorName = entity.contractorName,
            contractorContact = entity.contractorContact,
            architectName = entity.architectName,
            architectContact = entity.architectContact,
            createdDate = entity.createdDate,
            modifiedDate = entity.modifiedDate,
            // Computed properties
            totalExpenses = totalExpenses,
            expenseCount = expenseCount,
            budgetRemaining = entity.totalBudget - totalExpenses,
            budgetUtilization = if (entity.totalBudget > 0) (totalExpenses / entity.totalBudget * 100) else 0.0,
            isOverBudget = totalExpenses > entity.totalBudget,
            durationInDays = ((entity.actualEndDate ?: System.currentTimeMillis()) - entity.startDate) / (1000 * 60 * 60 * 24)
        )
    }

    /**
     * Convert Project domain model to ProjectEntity
     */
    fun toEntity(project: Project): ProjectEntity {
        return ProjectEntity(
            id = project.id,
            name = project.name,
            type = project.type.name,
            location = project.location,
            startDate = project.startDate,
            expectedEndDate = project.expectedEndDate,
            actualEndDate = project.actualEndDate,
            status = project.status.name,
            landCost = project.landCost,
            totalBudget = project.totalBudget,
            loanAmount = project.loanAmount,
            bankName = project.bankName,
            interestRate = project.interestRate,
            emiAmount = project.emiAmount,
            ownerName = project.ownerName,
            coOwnerName = project.coOwnerName,
            contractorName = project.contractorName,
            contractorContact = project.contractorContact,
            architectName = project.architectName,
            architectContact = project.architectContact,
            createdDate = project.createdDate,
            modifiedDate = project.modifiedDate
        )
    }

    /**
     * Convert list of ProjectEntity to list of Project
     *
     * @param entities List of entities to convert
     * @param expensesMap Map of projectId to (totalExpenses, expenseCount)
     */
    fun toDomainList(
        entities: List<ProjectEntity>,
        expensesMap: Map<String, Pair<Double, Int>> = emptyMap()
    ): List<Project> {
        return entities.map { entity ->
            val (totalExpenses, expenseCount) = expensesMap[entity.id] ?: (0.0 to 0)
            toDomain(entity, totalExpenses, expenseCount)
        }
    }
}
