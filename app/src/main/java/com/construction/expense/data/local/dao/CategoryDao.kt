package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Category operations.
 *
 * Categories are mostly static (12 default categories prepopulated).
 * Users can add custom categories, but this is rare.
 */
@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    /**
     * Insert multiple categories (used for prepopulation)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    /**
     * Get category by ID
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    fun getById(id: Int): Flow<CategoryEntity?>

    /**
     * Get all categories, ordered by sortOrder
     * This is the primary query used in UI
     */
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    /**
     * Get only default categories (not user-added)
     */
    @Query("SELECT * FROM categories WHERE isCustom = 0 ORDER BY sortOrder ASC")
    fun getDefault(): Flow<List<CategoryEntity>>

    /**
     * Get only custom user-added categories
     */
    @Query("SELECT * FROM categories WHERE isCustom = 1 ORDER BY name ASC")
    fun getCustom(): Flow<List<CategoryEntity>>

    /**
     * Check if categories have been prepopulated
     */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int

    /**
     * Get category by name (for validation)
     */
    @Query("SELECT * FROM categories WHERE name = :name COLLATE NOCASE")
    suspend fun getByName(name: String): CategoryEntity?
}
