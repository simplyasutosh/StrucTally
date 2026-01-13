package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.MilestoneEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Milestone operations.
 *
 * Milestones track project phases: Foundation, Structure, Finishing, etc.
 * Default milestones are created when a new project is created.
 * Users can add custom milestones or modify existing ones.
 */
@Dao
interface MilestoneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(milestone: MilestoneEntity): Long

    /**
     * Insert multiple milestones (for new projects)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(milestones: List<MilestoneEntity>)

    @Update
    suspend fun update(milestone: MilestoneEntity)

    @Delete
    suspend fun delete(milestone: MilestoneEntity)

    @Query("SELECT * FROM milestones WHERE id = :id")
    fun getById(id: Int): Flow<MilestoneEntity?>

    /**
     * Get all milestones for a project
     */
    @Query("""
        SELECT * FROM milestones
        WHERE projectId = :projectId
        ORDER BY plannedStartDate ASC
    """)
    fun getByProject(projectId: String): Flow<List<MilestoneEntity>>

    /**
     * Get milestones by status
     */
    @Query("""
        SELECT * FROM milestones
        WHERE projectId = :projectId AND status = :status
        ORDER BY plannedStartDate ASC
    """)
    fun getByStatus(projectId: String, status: String): Flow<List<MilestoneEntity>>

    /**
     * Get current/active milestone (status = IN_PROGRESS)
     */
    @Query("""
        SELECT * FROM milestones
        WHERE projectId = :projectId AND status = 'IN_PROGRESS'
        LIMIT 1
    """)
    fun getCurrent(projectId: String): Flow<MilestoneEntity?>

    /**
     * Get upcoming milestones (status = NOT_STARTED)
     */
    @Query("""
        SELECT * FROM milestones
        WHERE projectId = :projectId AND status = 'NOT_STARTED'
        ORDER BY plannedStartDate ASC
    """)
    fun getUpcoming(projectId: String): Flow<List<MilestoneEntity>>

    /**
     * Mark milestone as started
     */
    @Query("""
        UPDATE milestones
        SET status = 'IN_PROGRESS', actualStartDate = :startDate
        WHERE id = :milestoneId
    """)
    suspend fun markAsStarted(milestoneId: Int, startDate: Long)

    /**
     * Mark milestone as completed
     */
    @Query("""
        UPDATE milestones
        SET status = 'COMPLETED', actualEndDate = :endDate
        WHERE id = :milestoneId
    """)
    suspend fun markAsCompleted(milestoneId: Int, endDate: Long)
}
