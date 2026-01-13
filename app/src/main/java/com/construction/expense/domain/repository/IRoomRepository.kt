package com.construction.expense.domain.repository

import com.construction.expense.domain.model.RoomModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Room operations
 */
interface IRoomRepository {

    /**
     * Create new room
     */
    suspend fun createRoom(room: RoomModel): Result<Int>

    /**
     * Update room
     */
    suspend fun updateRoom(room: RoomModel): Result<Unit>

    /**
     * Delete room
     */
    suspend fun deleteRoom(roomId: Int): Result<Unit>

    /**
     * Get room by ID
     */
    fun getRoomById(roomId: Int): Flow<RoomModel?>

    /**
     * Get all rooms for project
     */
    fun getRoomsByProject(projectId: String): Flow<List<RoomModel>>

    /**
     * Get active rooms only
     */
    fun getActiveRoomsByProject(projectId: String): Flow<List<RoomModel>>
}
