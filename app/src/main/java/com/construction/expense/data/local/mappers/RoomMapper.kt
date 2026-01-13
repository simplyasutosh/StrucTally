package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.RoomEntity
import com.construction.expense.domain.model.RoomModel
import com.construction.expense.domain.model.RoomType

/**
 * Extension functions to convert between RoomEntity and RoomModel domain model.
 * Note: Domain model is called RoomModel to avoid conflict with Room database.
 */

fun RoomEntity.toDomain(): RoomModel {
    return RoomModel(
        id = id,
        projectId = projectId,
        name = name,
        type = RoomType.valueOf(type),
        floorLevel = floorLevel,
        squareFootage = squareFootage,
        budget = budget,
        isActive = isActive,
        sortOrder = sortOrder,
        createdDate = createdDate
    )
}

fun RoomModel.toEntity(): RoomEntity {
    return RoomEntity(
        id = id,
        projectId = projectId,
        name = name,
        type = type.name,
        floorLevel = floorLevel,
        squareFootage = squareFootage,
        budget = budget,
        isActive = isActive,
        sortOrder = sortOrder,
        createdDate = createdDate
    )
}

fun List<RoomEntity>.toDomainList(): List<RoomModel> {
    return map { it.toDomain() }
}
