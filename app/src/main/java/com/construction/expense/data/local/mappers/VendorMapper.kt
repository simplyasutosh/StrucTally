package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.VendorEntity
import com.construction.expense.domain.model.Vendor

/**
 * Extension functions to convert between VendorEntity and Vendor domain model.
 */

fun VendorEntity.toDomain(): Vendor {
    return Vendor(
        id = id,
        projectId = projectId,
        name = name,
        contact = contact,
        email = email,
        gstNumber = gstNumber,
        address = address,
        notes = notes,
        createdDate = createdDate
    )
}

fun Vendor.toEntity(): VendorEntity {
    return VendorEntity(
        id = id,
        projectId = projectId,
        name = name,
        contact = contact,
        email = email,
        gstNumber = gstNumber,
        address = address,
        notes = notes,
        createdDate = createdDate
    )
}

fun List<VendorEntity>.toDomainList(): List<Vendor> {
    return map { it.toDomain() }
}
