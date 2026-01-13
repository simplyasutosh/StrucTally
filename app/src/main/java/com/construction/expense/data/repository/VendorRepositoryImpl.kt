package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.VendorDao
import com.construction.expense.data.local.mappers.VendorMapper
import com.construction.expense.domain.model.Vendor
import com.construction.expense.domain.repository.IVendorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VendorRepositoryImpl @Inject constructor(
    private val vendorDao: VendorDao,
    private val vendorMapper: VendorMapper
) : IVendorRepository {

    override suspend fun createVendor(vendor: Vendor): Result<Int> {
        return try {
            val entity = vendorMapper.toEntity(vendor)
            val id = vendorDao.insert(entity).toInt()
            Result.success(id)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create vendor")
            Result.failure(e)
        }
    }

    override suspend fun updateVendor(vendor: Vendor): Result<Unit> {
        return try {
            val entity = vendorMapper.toEntity(vendor)
            vendorDao.update(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update vendor")
            Result.failure(e)
        }
    }

    override suspend fun deleteVendor(vendorId: Int): Result<Unit> {
        return try {
            vendorDao.deleteById(vendorId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete vendor")
            Result.failure(e)
        }
    }

    override fun getVendorById(vendorId: Int): Flow<Vendor?> {
        return vendorDao.getById(vendorId).map { it?.let { vendorMapper.toDomain(it) } }
    }

    override suspend fun getByName(projectId: String, vendorName: String): Vendor? {
        return try {
            vendorDao.getByName(projectId, vendorName)?.let {
                vendorMapper.toDomain(it)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get vendor by name")
            null
        }
    }

    override fun getVendorsByProject(projectId: String): Flow<List<Vendor>> {
        return vendorDao.getByProject(projectId).map { entities ->
            entities.map { vendorMapper.toDomain(it) }
        }
    }

    override fun searchVendors(projectId: String, query: String): Flow<List<Vendor>> {
        return vendorDao.search(projectId, "%$query%").map { entities ->
            entities.map { vendorMapper.toDomain(it) }
        }
    }
}
