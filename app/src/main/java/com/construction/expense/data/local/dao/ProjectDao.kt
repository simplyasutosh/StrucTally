package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Project operations.
 *
 * Provides methods to:
 * - Create, read, update, delete projects
 * - Query projects by status
 * - Get all projects
 * - Get project count
 *
 * All query methods return Flow for reactive updates.
 * All write operations are suspend functions.
 */
@Dao
interface ProjectDao {

    // ===== CREATE =====

    /**
     * Insert a new project.
     * @return Row ID of inserted project
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity): Long

    /**
     * Insert multiple projects (for bulk operations or testing)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<ProjectEntity>)

    // ===== UPDATE =====

    /**
     * Update an existing project
     */
    @Update
    suspend fun update(project: ProjectEntity)

    // ===== DELETE =====

    /**
     * Delete a project
     * Note: Cascade deletes all related expenses, rooms, budgets, etc.
     */
    @Delete
    suspend fun delete(project: ProjectEntity)

    // ===== QUERIES =====

    /**
     * Get project by ID.
     * Returns Flow so UI updates automatically when project changes.
     */
    @Query("SELECT * FROM projects WHERE id = :id")
    fun getById(id: String): Flow<ProjectEntity?>

    /**
     * Get all projects, ordered by most recent first
     */
    @Query("SELECT * FROM projects ORDER BY createdDate DESC")
    fun getAll(): Flow<List<ProjectEntity>>

    /**
     * Get only active projects (PLANNING, ACTIVE, ON_HOLD)
     * Excludes COMPLETED and ABANDONED projects
     */
    @Query("""
        SELECT * FROM projects
        WHERE status IN ('PLANNING', 'ACTIVE', 'ON_HOLD')
        ORDER BY createdDate DESC
    """)
    fun getActive(): Flow<List<ProjectEntity>>

    /**
     * Get projects by specific status
     */
    @Query("SELECT * FROM projects WHERE status = :status ORDER BY createdDate DESC")
    fun getByStatus(status: String): Flow<List<ProjectEntity>>

    /**
     * Get project count (for statistics)
     */
    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getCount(): Int

    /**
     * Get projects modified after a certain date (for sync)
     */
    @Query("SELECT * FROM projects WHERE modifiedDate > :timestamp")
    suspend fun getModifiedAfter(timestamp: Long): List<ProjectEntity>
}
