package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.CategoryEntity
import com.construction.expense.domain.model.Category

/**
 * Extension functions to convert between CategoryEntity and Category domain model.
 */

/**
 * Convert CategoryEntity to Category domain model
 */
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        hasRoomTracking = hasRoomTracking,
        sortOrder = sortOrder,
        typicalBudgetPercent = typicalBudgetPercent,
        isCustom = isCustom
    )
}

/**
 * Convert Category domain model to CategoryEntity
 */
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        hasRoomTracking = hasRoomTracking,
        sortOrder = sortOrder,
        typicalBudgetPercent = typicalBudgetPercent,
        isCustom = isCustom
    )
}

/**
 * Convert list of CategoryEntity to list of Category
 */
fun List<CategoryEntity>.toDomainList(): List<Category> {
    return map { it.toDomain() }
}
