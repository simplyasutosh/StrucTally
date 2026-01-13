package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.RoomDao
import com.construction.expense.data.local.mappers.RoomMapper
import com.construction.expense.domain.model.RoomModel
import com.construction.expense.domain.repository.IRoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val roomDao: RoomDao,
    private val roomMapper: RoomMapper
) : IRoomRepository {

    override suspend fun createRoom(room: RoomModel): Result<Int> {
        return try {
            val entity = roomMapper.toEntity(room)
            val id = roomDao.insert(entity).toInt()
            Result.success(id)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create room")
            Result.failure(e)
        }
    }

    override suspend fun updateRoom(room: RoomModel): Result<Unit> {
        return try {
            val entity = roomMapper.toEntity(room)
            roomDao.update(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update room")
            Result.failure(e)
        }
    }

    override suspend fun deleteRoom(roomId: Int): Result<Unit> {
        return try {
            roomDao.deleteById(roomId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete room")
            Result.failure(e)
        }
    }

    override fun getRoomById(roomId: Int): Flow<RoomModel?> {
        return roomDao.getById(roomId).map { it?.let { roomMapper.toDomain(it) } }
    }

    override fun getRoomsByProject(projectId: String): Flow<List<RoomModel>> {
        return roomDao.getByProject(projectId).map { entities ->
            entities.map { roomMapper.toDomain(it) }
        }
    }

    override fun getActiveRoomsByProject(projectId: String): Flow<List<RoomModel>> {
        return roomDao.getActiveByProject(projectId).map { entities ->
            entities.map { roomMapper.toDomain(it) }
        }
    }
}
