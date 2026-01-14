package com.construction.expense.data.local.mappers

import com.construction.expense.data.local.entities.ExpenseEntity
import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.model.ExpenseStatus
import com.construction.expense.domain.model.PaymentMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between ExpenseEntity and Expense domain model.
 */
@Singleton
class ExpenseMapper @Inject constructor() {

    /**
     * Convert ExpenseEntity to Expense domain model
     */
    fun toDomain(entity: ExpenseEntity): Expense {
        return Expense(
            id = entity.id,
            projectId = entity.projectId,
            date = entity.date,
            categoryId = entity.categoryId,
            categoryName = entity.categoryName,
            subCategoryId = entity.subCategoryId,
            subCategoryName = entity.subCategoryName,
            roomId = entity.roomId,
            roomName = entity.roomName,
            milestoneId = entity.milestoneId,
            milestoneName = entity.milestoneName,
            amount = entity.amount,
            vendorName = entity.vendorName,
            vendorContact = entity.vendorContact,
            paymentMode = PaymentMode.valueOf(entity.paymentMode),
            transactionId = entity.transactionId,
            invoiceNumber = entity.invoiceNumber,
            gstAmount = entity.gstAmount,
            vendorGst = entity.vendorGst,
            notes = entity.notes,
            receiptUrl = entity.receiptUrl,
            receiptThumbnailUrl = entity.receiptThumbnailUrl,
            isSynced = entity.isSynced,
            createdDate = entity.createdDate,
            modifiedDate = entity.modifiedDate,
            status = ExpenseStatus.valueOf(entity.status)
        )
    }

    /**
     * Convert Expense domain model to ExpenseEntity
     */
    fun toEntity(expense: Expense): ExpenseEntity {
        return ExpenseEntity(
            id = expense.id,
            projectId = expense.projectId,
            date = expense.date,
            categoryId = expense.categoryId,
            categoryName = expense.categoryName,
            subCategoryId = expense.subCategoryId,
            subCategoryName = expense.subCategoryName,
            roomId = expense.roomId,
            roomName = expense.roomName,
            milestoneId = expense.milestoneId,
            milestoneName = expense.milestoneName,
            amount = expense.amount,
            vendorName = expense.vendorName,
            vendorContact = expense.vendorContact,
            paymentMode = expense.paymentMode.name,
            transactionId = expense.transactionId,
            invoiceNumber = expense.invoiceNumber,
            gstAmount = expense.gstAmount,
            vendorGst = expense.vendorGst,
            notes = expense.notes,
            receiptUrl = expense.receiptUrl,
            receiptThumbnailUrl = expense.receiptThumbnailUrl,
            isSynced = expense.isSynced,
            createdDate = expense.createdDate,
            modifiedDate = expense.modifiedDate,
            status = expense.status.name
        )
    }

    /**
     * Convert list of ExpenseEntity to list of Expense
     */
    fun toDomainList(entities: List<ExpenseEntity>): List<Expense> {
        return entities.map { toDomain(it) }
    }
}
