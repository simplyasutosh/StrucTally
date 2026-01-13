package com.construction.expense.domain.model

/**
 * Data extracted from receipt via OCR
 */
data class ReceiptData(
    val rawText: String,
    val amount: Double? = null,
    val date: Long? = null,
    val vendorName: String? = null,
    val invoiceNumber: String? = null,
    val gstNumber: String? = null,
    val gstAmount: Double? = null,
    val items: List<LineItem>? = null,
    val confidence: Float = 0f
)

/**
 * Individual line item from receipt
 */
data class LineItem(
    val description: String,
    val quantity: Int,
    val price: Double
)
