package com.construction.expense.domain.repository

import com.construction.expense.domain.model.Category
import com.construction.expense.domain.model.CategoryWithSubCategories
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Category operations
 */
interface ICategoryRepository {

    /**
     * Get all categories
     */
    fun getAllCategories(): Flow<List<Category>>

    /**
     * Get category by ID
     */
    fun getCategoryById(categoryId: Int): Flow<Category?>

    /**
     * Get categories with their subcategories
     */
    fun getCategoriesWithSubCategories(): Flow<List<CategoryWithSubCategories>>
}
