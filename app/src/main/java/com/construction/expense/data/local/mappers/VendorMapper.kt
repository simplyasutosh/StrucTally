package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.VendorEntity
import com.construction.expense.domain.model.Vendor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between VendorEntity and Vendor domain model.
 */
@Singleton
class VendorMapper @Inject constructor() {

    /**
     * Convert VendorEntity to Vendor domain model
     */
    fun toDomain(entity: VendorEntity): Vendor {
        return Vendor(
            id = entity.id,
            projectId = entity.projectId,
            name = entity.name,
            contact = entity.contact,
            email = entity.email,
            gstNumber = entity.gstNumber,
            address = entity.address,
            notes = entity.notes,
            createdDate = entity.createdDate
        )
    }

    /**
     * Convert Vendor domain model to VendorEntity
     */
    fun toEntity(vendor: Vendor): VendorEntity {
        return VendorEntity(
            id = vendor.id,
            projectId = vendor.projectId,
            name = vendor.name,
            contact = vendor.contact,
            email = vendor.email,
            gstNumber = vendor.gstNumber,
            address = vendor.address,
            notes = vendor.notes,
            createdDate = vendor.createdDate
        )
    }

    /**
     * Convert list of VendorEntity to list of Vendor
     */
    fun toDomainList(entities: List<VendorEntity>): List<Vendor> {
        return entities.map { toDomain(it) }
    }
}
