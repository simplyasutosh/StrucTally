package com.construction.expense.domain.repository

import com.construction.expense.domain.model.Vendor
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Vendor operations
 */
interface IVendorRepository {

    /**
     * Create new vendor
     */
    suspend fun createVendor(vendor: Vendor): Result<Int>

    /**
     * Update vendor
     */
    suspend fun updateVendor(vendor: Vendor): Result<Unit>

    /**
     * Delete vendor
     */
    suspend fun deleteVendor(vendorId: Int): Result<Unit>

    /**
     * Get vendor by ID
     */
    fun getVendorById(vendorId: Int): Flow<Vendor?>

    /**
     * Get vendor by name
     */
    suspend fun getByName(projectId: String, vendorName: String): Vendor?

    /**
     * Get all vendors for project
     */
    fun getVendorsByProject(projectId: String): Flow<List<Vendor>>

    /**
     * Search vendors
     */
    fun searchVendors(projectId: String, query: String): Flow<List<Vendor>>
}
