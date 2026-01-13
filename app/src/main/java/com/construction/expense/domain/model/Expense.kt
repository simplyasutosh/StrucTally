package com.construction.expense.domain.model

data class Expense(
    val id: String,
    val projectId: String,
    val date: Long,

    // Category & Room
    val categoryId: Int,
    val categoryName: String,
    val subCategoryId: String,
    val subCategoryName: String,
    val roomId: Int? = null,
    val roomName: String? = null,

    // Milestone
    val milestoneId: Int? = null,
    val milestoneName: String? = null,

    // Financial
    val amount: Double,

    // Vendor
    val vendorName: String,
    val vendorContact: String? = null,

    // Payment
    val paymentMode: PaymentMode,
    val transactionId: String? = null,
    val invoiceNumber: String? = null,

    // GST
    val gstAmount: Double? = null,
    val vendorGst: String? = null,

    // Additional
    val notes: String? = null,

    // Receipt
    val receiptUrl: String? = null,
    val receiptThumbnailUrl: String? = null,

    // Sync
    val isSynced: Boolean = false,

    // Metadata
    val createdDate: Long,
    val modifiedDate: Long,
    val status: ExpenseStatus
) {
    /**
     * Formatted amount with rupee symbol
     */
    fun getFormattedAmount(): String {
        return "₹${String.format("%,.2f", amount)}"
    }

    /**
     * Display text for expense (for lists)
     */
    fun getDisplayText(): String {
        return "$categoryName - $subCategoryName${roomName?.let { " ($it)" } ?: ""}"
    }
}

enum class PaymentMode {
    CASH,
    UPI,
    CREDIT_CARD,
    DEBIT_CARD,
    CHEQUE,
    BANK_TRANSFER,
    ONLINE;

    fun getDisplayName(): String {
        return when (this) {
            CASH -> "Cash"
            UPI -> "UPI"
            CREDIT_CARD -> "Credit Card"
            DEBIT_CARD -> "Debit Card"
            CHEQUE -> "Cheque"
            BANK_TRANSFER -> "Bank Transfer"
            ONLINE -> "Online Payment"
        }
    }
}

enum class ExpenseStatus {
    ACTIVE,
    DELETED
}
