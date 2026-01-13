package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.SubCategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SubCategory operations.
 *
 * Subcategories are mostly static (80 default subcategories prepopulated).
 * The most common operation is getByCategory() to show subcategories
 * when user selects a main category.
 */
@Dao
interface SubCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subCategory: SubCategoryEntity): Long

    /**
     * Insert multiple subcategories (for prepopulation)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subCategories: List<SubCategoryEntity>)

    @Update
    suspend fun update(subCategory: SubCategoryEntity)

    @Delete
    suspend fun delete(subCategory: SubCategoryEntity)

    @Query("SELECT * FROM sub_categories WHERE id = :id")
    fun getById(id: String): Flow<SubCategoryEntity?>

    /**
     * Get all subcategories (rarely used - usually filter by category)
     */
    @Query("SELECT * FROM sub_categories ORDER BY categoryId, sortOrder ASC")
    fun getAll(): Flow<List<SubCategoryEntity>>

    /**
     * MOST IMPORTANT QUERY: Get subcategories for a specific category
     * Used in Add Expense screen when user selects a category
     *
     * Example: User selects "Flooring Contractor" (id=2)
     * This returns: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8
     */
    @Query("""
        SELECT * FROM sub_categories
        WHERE categoryId = :categoryId
        ORDER BY sortOrder ASC
    """)
    fun getByCategory(categoryId: Int): Flow<List<SubCategoryEntity>>

    /**
     * Get count (for validation after prepopulation)
     */
    @Query("SELECT COUNT(*) FROM sub_categories")
    suspend fun getCount(): Int

    /**
     * Get only default subcategories
     */
    @Query("""
        SELECT * FROM sub_categories
        WHERE isCustom = 0
        ORDER BY categoryId, sortOrder ASC
    """)
    fun getDefault(): Flow<List<SubCategoryEntity>>

    /**
     * Get only custom user-added subcategories
     */
    @Query("""
        SELECT * FROM sub_categories
        WHERE isCustom = 1
        ORDER BY name ASC
    """)
    fun getCustom(): Flow<List<SubCategoryEntity>>
}
