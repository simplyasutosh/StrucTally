package com.construction.expense.domain.model

data class Milestone(
    val id: Int,
    val projectId: String,
    val name: String,
    val plannedStartDate: Long? = null,
    val plannedEndDate: Long? = null,
    val actualStartDate: Long? = null,
    val actualEndDate: Long? = null,
    val status: MilestoneStatus,
    val budget: Double? = null,
    val notes: String? = null
)

enum class MilestoneStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    DELAYED
}
