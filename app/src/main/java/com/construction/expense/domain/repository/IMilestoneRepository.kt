package com.construction.expense.domain.repository

import com.construction.expense.domain.model.Milestone
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Milestone operations
 */
interface IMilestoneRepository {

    /**
     * Create new milestone
     */
    suspend fun createMilestone(milestone: Milestone): Result<Int>

    /**
     * Update milestone
     */
    suspend fun updateMilestone(milestone: Milestone): Result<Unit>

    /**
     * Delete milestone
     */
    suspend fun deleteMilestone(milestoneId: Int): Result<Unit>

    /**
     * Get milestone by ID
     */
    fun getMilestoneById(milestoneId: Int): Flow<Milestone?>

    /**
     * Get all milestones for project
     */
    fun getMilestonesByProject(projectId: String): Flow<List<Milestone>>

    /**
     * Get active milestones only
     */
    fun getActiveMilestonesByProject(projectId: String): Flow<List<Milestone>>
}
