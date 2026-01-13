package com.construction.expense.data.local.dao

import androidx.room.*
import com.construction.expense.data.local.entities.VendorEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Vendor operations.
 *
 * Vendors are automatically created when adding expenses with new vendor names.
 * This DAO helps with:
 * - Auto-suggest vendor names (avoid retyping)
 * - Vendor-wise spending analysis
 * - Maintaining vendor contact directory
 */
@Dao
interface VendorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vendor: VendorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vendors: List<VendorEntity>)

    @Update
    suspend fun update(vendor: VendorEntity)

    @Delete
    suspend fun delete(vendor: VendorEntity)

    @Query("SELECT * FROM vendors WHERE id = :id")
    fun getById(id: Int): Flow<VendorEntity?>

    /**
     * Get all vendors for a project (alphabetically)
     */
    @Query("""
        SELECT * FROM vendors
        WHERE projectId = :projectId
        ORDER BY name ASC
    """)
    fun getByProject(projectId: String): Flow<List<VendorEntity>>

    /**
     * IMPORTANT: Check if vendor already exists by name
     * Used before auto-creating vendor from expense
     */
    @Query("""
        SELECT * FROM vendors
        WHERE projectId = :projectId AND name = :name COLLATE NOCASE
    """)
    suspend fun getByName(projectId: String, name: String): VendorEntity?

    /**
     * Search vendors by name (for autocomplete/suggestions)
     * Partial match, case-insensitive
     */
    @Query("""
        SELECT * FROM vendors
        WHERE projectId = :projectId AND name LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun search(projectId: String, query: String): Flow<List<VendorEntity>>

    /**
     * Get vendors with GST number (for compliance tracking)
     */
    @Query("""
        SELECT * FROM vendors
        WHERE projectId = :projectId AND gstNumber IS NOT NULL
        ORDER BY name ASC
    """)
    fun getWithGst(projectId: String): Flow<List<VendorEntity>>

    /**
     * Get vendors without contact info (for cleanup)
     */
    @Query("""
        SELECT * FROM vendors
        WHERE projectId = :projectId AND (contact IS NULL OR contact = '')
        ORDER BY name ASC
    """)
    fun getWithoutContact(projectId: String): Flow<List<VendorEntity>>
}
