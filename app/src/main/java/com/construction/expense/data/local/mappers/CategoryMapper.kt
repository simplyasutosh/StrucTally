package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.CategoryEntity
import com.construction.expense.domain.model.Category
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between CategoryEntity and Category domain model.
 */
@Singleton
class CategoryMapper @Inject constructor() {

    /**
     * Convert CategoryEntity to Category domain model
     */
    fun toDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            icon = entity.icon,
            color = entity.color,
            hasRoomTracking = entity.hasRoomTracking,
            sortOrder = entity.sortOrder,
            typicalBudgetPercent = entity.typicalBudgetPercent,
            isCustom = entity.isCustom
        )
    }

    /**
     * Convert Category domain model to CategoryEntity
     */
    fun toEntity(category: Category): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            icon = category.icon,
            color = category.color,
            hasRoomTracking = category.hasRoomTracking,
            sortOrder = category.sortOrder,
            typicalBudgetPercent = category.typicalBudgetPercent,
            isCustom = category.isCustom
        )
    }

    /**
     * Convert list of CategoryEntity to list of Category
     */
    fun toDomainList(entities: List<CategoryEntity>): List<Category> {
        return entities.map { toDomain(it) }
    }
}
