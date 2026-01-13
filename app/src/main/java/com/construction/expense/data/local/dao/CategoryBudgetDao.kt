package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for CategoryBudget operations.
 *
 * Category budgets allow granular budget tracking per category.
 * Instead of just tracking total project budget, users can set budgets
 * for each of the 12 categories and get alerts when approaching limits.
 */
@Dao
interface CategoryBudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: CategoryBudgetEntity): Long

    /**
     * Insert multiple budgets (when setting up project budgets)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<CategoryBudgetEntity>)

    @Update
    suspend fun update(budget: CategoryBudgetEntity)

    @Delete
    suspend fun delete(budget: CategoryBudgetEntity)

    @Query("SELECT * FROM category_budgets WHERE id = :id")
    fun getById(id: Int): Flow<CategoryBudgetEntity?>

    /**
     * Get all category budgets for a project
     * Most common query - used in budget overview screens
     */
    @Query("""
        SELECT * FROM category_budgets
        WHERE projectId = :projectId
        ORDER BY categoryId ASC
    """)
    fun getByProject(projectId: String): Flow<List<CategoryBudgetEntity>>

    /**
     * IMPORTANT: Get budget for specific category
     * Used when adding expense to check budget alerts
     */
    @Query("""
        SELECT * FROM category_budgets
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    fun getBudget(projectId: String, categoryId: Int): Flow<CategoryBudgetEntity?>

    /**
     * Same as above but suspend (one-shot)
     */
    @Query("""
        SELECT * FROM category_budgets
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun getBudgetSync(projectId: String, categoryId: Int): CategoryBudgetEntity?

    /**
     * Update budget amount
     */
    @Query("""
        UPDATE category_budgets
        SET budgetedAmount = :amount, modifiedDate = :timestamp
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun updateBudgetAmount(
        projectId: String,
        categoryId: Int,
        amount: Double,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Toggle alert threshold on/off
     */
    @Query("""
        UPDATE category_budgets
        SET alertThreshold50 = :enabled
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun setAlert50(projectId: String, categoryId: Int, enabled: Boolean)

    @Query("""
        UPDATE category_budgets
        SET alertThreshold75 = :enabled
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun setAlert75(projectId: String, categoryId: Int, enabled: Boolean)

    @Query("""
        UPDATE category_budgets
        SET alertThreshold90 = :enabled
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun setAlert90(projectId: String, categoryId: Int, enabled: Boolean)

    @Query("""
        UPDATE category_budgets
        SET alertThreshold100 = :enabled
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun setAlert100(projectId: String, categoryId: Int, enabled: Boolean)

    /**
     * Set custom threshold percentage
     */
    @Query("""
        UPDATE category_budgets
        SET customThreshold = :threshold
        WHERE projectId = :projectId AND categoryId = :categoryId
    """)
    suspend fun setCustomThreshold(
        projectId: String,
        categoryId: Int,
        threshold: Int?
    )

    /**
     * Delete all budgets for a project (cascade handles this, but useful for cleanup)
     */
    @Query("DELETE FROM category_budgets WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: String)
}
