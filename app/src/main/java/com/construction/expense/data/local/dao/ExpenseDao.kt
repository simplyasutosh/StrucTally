package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Expense operations.
 *
 * This is the most complex DAO with many query methods for:
 * - CRUD operations
 * - Filtering (by project, category, room, date, vendor)
 * - Aggregations (totals, counts, averages)
 * - Search functionality
 * - Sync status queries
 *
 * All expenses are soft-deleted (status = 'DELETED') for audit trail.
 */
@Dao
interface ExpenseDao {

    // ===== CREATE =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)

    // ===== UPDATE =====

    @Update
    suspend fun update(expense: ExpenseEntity)

    // ===== DELETE =====

    /**
     * Hard delete (removes from database completely)
     * Usually not used - prefer softDelete for audit trail
     */
    @Delete
    suspend fun delete(expense: ExpenseEntity)

    /**
     * Soft delete - sets status to 'DELETED' but keeps record
     * Preferred method for deleting expenses
     */
    @Query("UPDATE expenses SET status = 'DELETED', modifiedDate = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Restore a soft-deleted expense
     */
    @Query("UPDATE expenses SET status = 'ACTIVE', modifiedDate = :timestamp WHERE id = :id")
    suspend fun restore(id: String, timestamp: Long = System.currentTimeMillis())

    // ===== BASIC QUERIES =====

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getById(id: String): Flow<ExpenseEntity?>

    /**
     * Get all ACTIVE expenses for a project (excludes deleted)
     * Ordered by most recent first
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByProject(projectId: String): Flow<List<ExpenseEntity>>

    /**
     * Get recent expenses with limit (for dashboard)
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
        ORDER BY date DESC
        LIMIT :limit
    """)
    fun getRecent(projectId: String, limit: Int): Flow<List<ExpenseEntity>>

    // ===== FILTERED QUERIES =====

    /**
     * Get expenses by category
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND categoryId = :categoryId
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByCategory(projectId: String, categoryId: Int): Flow<List<ExpenseEntity>>

    /**
     * Get expenses by subcategory
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND subCategoryId = :subCategoryId
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getBySubCategory(projectId: String, subCategoryId: String): Flow<List<ExpenseEntity>>

    /**
     * Get expenses by room
     * Important: Civil Contractor expenses have roomId = NULL
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND roomId = :roomId
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByRoom(projectId: String, roomId: Int): Flow<List<ExpenseEntity>>

    /**
     * Get expenses by date range
     * Useful for: monthly reports, weekly summaries, custom date filters
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND date BETWEEN :startDate AND :endDate
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByDateRange(
        projectId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<ExpenseEntity>>

    /**
     * Get expenses by vendor (partial match for search)
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND vendorName LIKE '%' || :vendorName || '%'
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByVendor(projectId: String, vendorName: String): Flow<List<ExpenseEntity>>

    /**
     * Get expenses by payment mode
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND paymentMode = :paymentMode
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByPaymentMode(projectId: String, paymentMode: String): Flow<List<ExpenseEntity>>

    /**
     * Get expenses by milestone/phase
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND milestoneId = :milestoneId
        AND status = 'ACTIVE'
        ORDER BY date DESC
    """)
    fun getByMilestone(projectId: String, milestoneId: Int): Flow<List<ExpenseEntity>>

    // ===== AGGREGATION QUERIES =====

    /**
     * Get total expenses for entire project
     * Returns 0.0 if no expenses
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
    """)
    fun getTotalByProject(projectId: String): Flow<Double>

    /**
     * Get total for specific category
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expenses
        WHERE projectId = :projectId
        AND categoryId = :categoryId
        AND status = 'ACTIVE'
    """)
    fun getTotalByCategory(projectId: String, categoryId: Int): Flow<Double>

    /**
     * Get total for specific room
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expenses
        WHERE projectId = :projectId
        AND roomId = :roomId
        AND status = 'ACTIVE'
    """)
    fun getTotalByRoom(projectId: String, roomId: Int): Flow<Double>

    /**
     * Get expense count for project
     */
    @Query("""
        SELECT COUNT(*)
        FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
    """)
    fun getCountByProject(projectId: String): Flow<Int>

    /**
     * Get average expense amount
     */
    @Query("""
        SELECT COALESCE(AVG(amount), 0.0)
        FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
    """)
    fun getAverageByProject(projectId: String): Flow<Double>

    // ===== SEARCH =====

    /**
     * Full-text search across multiple fields
     * Searches in: category, subcategory, vendor name, notes
     */
    @Query("""
        SELECT * FROM expenses
        WHERE projectId = :projectId
        AND status = 'ACTIVE'
        AND (
            categoryName LIKE '%' || :query || '%'
            OR subCategoryName LIKE '%' || :query || '%'
            OR vendorName LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
            OR CAST(amount AS TEXT) LIKE '%' || :query || '%'
        )
        ORDER BY date DESC
    """)
    fun search(projectId: String, query: String): Flow<List<ExpenseEntity>>

    // ===== SYNC QUERIES =====

    /**
     * Get expenses that haven't been synced to cloud yet
     */
    @Query("SELECT * FROM expenses WHERE isSynced = 0")
    fun getUnsynced(): Flow<List<ExpenseEntity>>

    /**
     * Mark expenses as synced
     */
    @Query("UPDATE expenses SET isSynced = 1 WHERE id IN (:expenseIds)")
    suspend fun markAsSynced(expenseIds: List<String>)

    /**
     * Get expenses modified after a timestamp (for incremental sync)
     */
    @Query("SELECT * FROM expenses WHERE modifiedDate > :timestamp")
    suspend fun getModifiedAfter(timestamp: Long): List<ExpenseEntity>

    // ===== COMPLEX QUERIES FOR REPORTS =====

    /**
     * Get monthly expense totals
     * Groups expenses by month for trend analysis
     */
    @Query("""
        SELECT
            strftime('%Y-%m', date/1000, 'unixepoch') as month,
            SUM(amount) as total,
            COUNT(*) as count
        FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
        GROUP BY month
        ORDER BY month DESC
    """)
    suspend fun getMonthlyTotals(projectId: String): List<MonthlyTotal>

    /**
     * Get category-wise totals
     */
    @Query("""
        SELECT
            categoryId,
            categoryName,
            SUM(amount) as total,
            COUNT(*) as count
        FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
        GROUP BY categoryId
        ORDER BY total DESC
    """)
    suspend fun getCategoryTotals(projectId: String): List<CategoryTotal>

    /**
     * Get vendor-wise totals
     */
    @Query("""
        SELECT
            vendorName,
            SUM(amount) as total,
            COUNT(*) as count
        FROM expenses
        WHERE projectId = :projectId AND status = 'ACTIVE'
        GROUP BY vendorName
        ORDER BY total DESC
    """)
    suspend fun getVendorTotals(projectId: String): List<VendorTotal>
}

/**
 * Data classes for complex query results
 */

/**
 * Monthly expense totals for trend analysis
 */
data class MonthlyTotal(
    val month: String,
    val total: Double,
    val count: Int
)

/**
 * Category-wise expense totals
 */
data class CategoryTotal(
    val categoryId: Int,
    val categoryName: String,
    val total: Double,
    val count: Int
)

/**
 * Vendor-wise expense totals
 */
data class VendorTotal(
    val vendorName: String,
    val total: Double,
    val count: Int
)
