package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.SubCategoryEntity
import com.construction.expense.domain.model.SubCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between SubCategoryEntity and SubCategory domain model.
 */
@Singleton
class SubCategoryMapper @Inject constructor() {

    /**
     * Convert SubCategoryEntity to SubCategory domain model
     */
    fun toDomain(entity: SubCategoryEntity): SubCategory {
        return SubCategory(
            id = entity.id,
            categoryId = entity.categoryId,
            name = entity.name,
            sortOrder = entity.sortOrder,
            isCustom = entity.isCustom
        )
    }

    /**
     * Convert SubCategory domain model to SubCategoryEntity
     */
    fun toEntity(subCategory: SubCategory): SubCategoryEntity {
        return SubCategoryEntity(
            id = subCategory.id,
            categoryId = subCategory.categoryId,
            name = subCategory.name,
            sortOrder = subCategory.sortOrder,
            isCustom = subCategory.isCustom
        )
    }

    /**
     * Convert list of SubCategoryEntity to list of SubCategory
     */
    fun toDomainList(entities: List<SubCategoryEntity>): List<SubCategory> {
        return entities.map { toDomain(it) }
    }
}
