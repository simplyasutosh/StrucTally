package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.RoomEntity
import com.construction.expense.domain.model.RoomModel
import com.construction.expense.domain.model.RoomType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between RoomEntity and RoomModel domain model.
 * Note: Domain model is called RoomModel to avoid conflict with Room database.
 */
@Singleton
class RoomMapper @Inject constructor() {

    /**
     * Convert RoomEntity to RoomModel domain model
     */
    fun toDomain(entity: RoomEntity): RoomModel {
        return RoomModel(
            id = entity.id,
            projectId = entity.projectId,
            name = entity.name,
            type = RoomType.valueOf(entity.type),
            floorLevel = entity.floorLevel,
            squareFootage = entity.squareFootage,
            budget = entity.budget,
            isActive = entity.isActive,
            sortOrder = entity.sortOrder,
            createdDate = entity.createdDate
        )
    }

    /**
     * Convert RoomModel domain model to RoomEntity
     */
    fun toEntity(room: RoomModel): RoomEntity {
        return RoomEntity(
            id = room.id,
            projectId = room.projectId,
            name = room.name,
            type = room.type.name,
            floorLevel = room.floorLevel,
            squareFootage = room.squareFootage,
            budget = room.budget,
            isActive = room.isActive,
            sortOrder = room.sortOrder,
            createdDate = room.createdDate
        )
    }

    /**
     * Convert list of RoomEntity to list of RoomModel
     */
    fun toDomainList(entities: List<RoomEntity>): List<RoomModel> {
        return entities.map { toDomain(it) }
    }
}
