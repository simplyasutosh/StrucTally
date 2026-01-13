package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.SubCategoryEntity
import com.construction.expense.domain.model.SubCategory

/**
 * Extension functions to convert between SubCategoryEntity and SubCategory domain model.
 */

fun SubCategoryEntity.toDomain(): SubCategory {
    return SubCategory(
        id = id,
        categoryId = categoryId,
        name = name,
        sortOrder = sortOrder,
        isCustom = isCustom
    )
}

fun SubCategory.toEntity(): SubCategoryEntity {
    return SubCategoryEntity(
        id = id,
        categoryId = categoryId,
        name = name,
        sortOrder = sortOrder,
        isCustom = isCustom
    )
}

fun List<SubCategoryEntity>.toDomainList(): List<SubCategory> {
    return map { it.toDomain() }
}
