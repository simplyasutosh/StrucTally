package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.MilestoneEntity
import com.construction.expense.domain.model.Milestone
import com.construction.expense.domain.model.MilestoneStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between MilestoneEntity and Milestone domain model.
 */
@Singleton
class MilestoneMapper @Inject constructor() {

    /**
     * Convert MilestoneEntity to Milestone domain model
     */
    fun toDomain(entity: MilestoneEntity): Milestone {
        return Milestone(
            id = entity.id,
            projectId = entity.projectId,
            name = entity.name,
            plannedStartDate = entity.plannedStartDate,
            plannedEndDate = entity.plannedEndDate,
            actualStartDate = entity.actualStartDate,
            actualEndDate = entity.actualEndDate,
            status = MilestoneStatus.valueOf(entity.status),
            budget = entity.budget,
            notes = entity.notes
        )
    }

    /**
     * Convert Milestone domain model to MilestoneEntity
     */
    fun toEntity(milestone: Milestone): MilestoneEntity {
        return MilestoneEntity(
            id = milestone.id,
            projectId = milestone.projectId,
            name = milestone.name,
            plannedStartDate = milestone.plannedStartDate,
            plannedEndDate = milestone.plannedEndDate,
            actualStartDate = milestone.actualStartDate,
            actualEndDate = milestone.actualEndDate,
            status = milestone.status.name,
            budget = milestone.budget,
            notes = milestone.notes
        )
    }

    /**
     * Convert list of MilestoneEntity to list of Milestone
     */
    fun toDomainList(entities: List<MilestoneEntity>): List<Milestone> {
        return entities.map { toDomain(it) }
    }
}
