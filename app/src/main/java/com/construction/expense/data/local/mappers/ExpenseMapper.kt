package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.ExpenseEntity
import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.model.ExpenseStatus
import com.construction.expense.domain.model.PaymentMode

/**
 * Extension functions to convert between ExpenseEntity and Expense domain model.
 */

/**
 * Convert ExpenseEntity to Expense domain model
 */
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        projectId = projectId,
        date = date,
        categoryId = categoryId,
        categoryName = categoryName,
        subCategoryId = subCategoryId,
        subCategoryName = subCategoryName,
        roomId = roomId,
        roomName = roomName,
        milestoneId = milestoneId,
        milestoneName = milestoneName,
        amount = amount,
        vendorName = vendorName,
        vendorContact = vendorContact,
        paymentMode = PaymentMode.valueOf(paymentMode),
        transactionId = transactionId,
        invoiceNumber = invoiceNumber,
        gstAmount = gstAmount,
        vendorGst = vendorGst,
        notes = notes,
        receiptUrl = receiptUrl,
        receiptThumbnailUrl = receiptThumbnailUrl,
        isSynced = isSynced,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        status = ExpenseStatus.valueOf(status)
    )
}

/**
 * Convert Expense domain model to ExpenseEntity
 */
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        projectId = projectId,
        date = date,
        categoryId = categoryId,
        categoryName = categoryName,
        subCategoryId = subCategoryId,
        subCategoryName = subCategoryName,
        roomId = roomId,
        roomName = roomName,
        milestoneId = milestoneId,
        milestoneName = milestoneName,
        amount = amount,
        vendorName = vendorName,
        vendorContact = vendorContact,
        paymentMode = paymentMode.name,
        transactionId = transactionId,
        invoiceNumber = invoiceNumber,
        gstAmount = gstAmount,
        vendorGst = vendorGst,
        notes = notes,
        receiptUrl = receiptUrl,
        receiptThumbnailUrl = receiptThumbnailUrl,
        isSynced = isSynced,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        status = status.name
    )
}

/**
 * Convert list of ExpenseEntity to list of Expense
 */
fun List<ExpenseEntity>.toDomainList(): List<Expense> {
    return map { it.toDomain() }
}
