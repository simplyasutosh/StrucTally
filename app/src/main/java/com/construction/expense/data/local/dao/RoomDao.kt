package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.RoomEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Room operations.
 *
 * Rooms are project-specific. Each project has its own set of rooms.
 * Default rooms are created when a new project is created.
 * Users can add custom rooms, rename rooms, or deactivate unused rooms.
 */
@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(room: RoomEntity): Long

    /**
     * Insert multiple rooms (used when creating new project)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rooms: List<RoomEntity>)

    @Update
    suspend fun update(room: RoomEntity)

    @Delete
    suspend fun delete(room: RoomEntity)

    /**
     * Delete room by ID
     */
    @Query("DELETE FROM rooms WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM rooms WHERE id = :id")
    fun getById(id: Int): Flow<RoomEntity?>

    /**
     * MOST IMPORTANT QUERY: Get all rooms for a project
     * Used in Add Expense screen for room selection
     */
    @Query("""
        SELECT * FROM rooms
        WHERE projectId = :projectId
        ORDER BY sortOrder ASC
    """)
    fun getByProject(projectId: String): Flow<List<RoomEntity>>

    /**
     * Get only active rooms (excludes deactivated rooms)
     * Used in expense entry - don't show rooms that user has disabled
     */
    @Query("""
        SELECT * FROM rooms
        WHERE projectId = :projectId AND isActive = 1
        ORDER BY sortOrder ASC
    """)
    fun getActiveByProject(projectId: String): Flow<List<RoomEntity>>

    /**
     * Get rooms by type (INDOOR, OUTDOOR, COMMON)
     */
    @Query("""
        SELECT * FROM rooms
        WHERE projectId = :projectId AND type = :type
        ORDER BY sortOrder ASC
    """)
    fun getByType(projectId: String, type: String): Flow<List<RoomEntity>>

    /**
     * Get rooms by floor level
     */
    @Query("""
        SELECT * FROM rooms
        WHERE projectId = :projectId AND floorLevel = :floorLevel
        ORDER BY sortOrder ASC
    """)
    fun getByFloor(projectId: String, floorLevel: String): Flow<List<RoomEntity>>

    /**
     * Deactivate a room (don't show in dropdowns, but keep expenses)
     */
    @Query("UPDATE rooms SET isActive = 0 WHERE id = :roomId")
    suspend fun deactivate(roomId: Int)

    /**
     * Reactivate a room
     */
    @Query("UPDATE rooms SET isActive = 1 WHERE id = :roomId")
    suspend fun activate(roomId: Int)

    /**
     * Check if room name already exists in project
     */
    @Query("""
        SELECT * FROM rooms
        WHERE projectId = :projectId AND name = :name COLLATE NOCASE
    """)
    suspend fun getByName(projectId: String, name: String): RoomEntity?
}
