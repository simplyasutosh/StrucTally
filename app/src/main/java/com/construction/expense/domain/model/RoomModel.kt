package com.construction.expense.domain.model

data class RoomModel(
    val id: Int,
    val projectId: String,
    val name: String,
    val type: RoomType,
    val floorLevel: String? = null,
    val squareFootage: Double? = null,
    val budget: Double? = null,
    val isActive: Boolean,
    val sortOrder: Int,
    val createdDate: Long
)

enum class RoomType {
    INDOOR,
    OUTDOOR,
    COMMON
}
