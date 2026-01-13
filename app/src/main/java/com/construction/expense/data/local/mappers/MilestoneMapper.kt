package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.MilestoneEntity
import com.construction.expense.domain.model.Milestone
import com.construction.expense.domain.model.MilestoneStatus

/**
 * Extension functions to convert between MilestoneEntity and Milestone domain model.
 */

fun MilestoneEntity.toDomain(): Milestone {
    return Milestone(
        id = id,
        projectId = projectId,
        name = name,
        plannedStartDate = plannedStartDate,
        plannedEndDate = plannedEndDate,
        actualStartDate = actualStartDate,
        actualEndDate = actualEndDate,
        status = MilestoneStatus.valueOf(status),
        budget = budget,
        notes = notes
    )
}

fun Milestone.toEntity(): MilestoneEntity {
    return MilestoneEntity(
        id = id,
        projectId = projectId,
        name = name,
        plannedStartDate = plannedStartDate,
        plannedEndDate = plannedEndDate,
        actualStartDate = actualStartDate,
        actualEndDate = actualEndDate,
        status = status.name,
        budget = budget,
        notes = notes
    )
}

fun List<MilestoneEntity>.toDomainList(): List<Milestone> {
    return map { it.toDomain() }
}
