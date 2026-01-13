package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single expense/purchase.
 *
 * This is the core of the app - each expense tracks:
 * - What was bought (category/subcategory)
 * - Where it was used (room/area)
 * - How much it cost
 * - Who was paid (vendor)
 * - Proof of purchase (receipt photo)
 *
 * Examples:
 * - "Bought 50 bags of cement for ₹45,600 from Shree Suppliers"
 * - "Paid electrician ₹15,000 for master bedroom wiring"
 * - "Flooring tiles for living room - ₹85,000 from Tile World"
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE // Delete expenses when project is deleted
        )
    ],
    indices = [
        Index("projectId"),     // For fast project-based queries
        Index("date"),          // For date range queries
        Index("categoryId"),    // For category filtering
        Index("roomId"),        // For room-based queries
        Index("vendorName")     // For vendor searches
    ]
)
data class ExpenseEntity(
    @PrimaryKey
    val id: String, // UUID string

    // ===== PROJECT LINKAGE =====
    /**
     * Foreign key reference to the project this expense belongs to
     * Links to [ProjectEntity.id]
     */
    val projectId: String,

    // ===== TIMING =====
    /**
     * Date when the expense occurred as Unix timestamp in milliseconds
     * This is when the purchase was made or payment was done
     */
    val date: Long,

    // ===== CATEGORIZATION =====
    /**
     * Category ID (1-12) linking to [CategoryEntity]
     * Categories follow contractor structure: Civil, Flooring, Electrical, etc.
     */
    val categoryId: Int,

    /**
     * Denormalized category name for display without joins
     * Example: "Flooring Contractor", "Electrical Contractor"
     * Stored redundantly for performance and offline support
     */
    val categoryName: String,

    /**
     * Subcategory ID in format "X.Y" (e.g., "2.1", "3.4")
     * Links to SubCategoryEntity for detailed classification
     * Example: "2.1" = Living Areas Flooring under Flooring Contractor
     */
    val subCategoryId: String,

    /**
     * Denormalized subcategory name for display
     * Example: "Living Areas Flooring", "Master Bedroom Electrical"
     * Stored redundantly for performance
     */
    val subCategoryName: String,

    // ===== ROOM/AREA =====
    /**
     * Room/area ID where this expense applies
     * Links to RoomEntity
     *
     * NULL for Civil Contractor (structure work applies to whole building)
     * REQUIRED for all other categories (flooring, electrical, plumbing, etc.)
     */
    val roomId: Int? = null,

    /**
     * Denormalized room name for display
     * Example: "Living Room", "Master Bedroom", "Kitchen"
     * NULL for expenses that don't track rooms (Civil Contractor)
     */
    val roomName: String? = null,

    // ===== PROJECT PHASE (optional) =====
    /**
     * Optional milestone/phase ID for project timeline tracking
     * Links to MilestoneEntity
     * Examples: Foundation Phase, Structure Phase, Finishing Phase
     */
    val milestoneId: Int? = null,

    /**
     * Denormalized milestone name for display
     * Example: "Foundation Work", "Interior Finishing"
     */
    val milestoneName: String? = null,

    // ===== FINANCIAL =====
    /**
     * Expense amount in INR (₹)
     * This is the total amount paid including taxes
     */
    val amount: Double,

    // ===== VENDOR/SUPPLIER =====
    /**
     * Name of vendor/supplier who was paid
     * Example: "Shree Cement Suppliers", "Ram Electrical Works"
     * Used for vendor analysis and repeat purchases
     */
    val vendorName: String,

    /**
     * Vendor phone number for contact
     * Optional - useful for reordering or follow-ups
     */
    val vendorContact: String? = null,

    // ===== PAYMENT DETAILS =====
    /**
     * Payment method used - maps to [PaymentMode] enum
     * Examples: CASH, UPI, CREDIT_CARD, CHEQUE
     * Stored as string for flexibility
     */
    val paymentMode: String,

    /**
     * Transaction reference ID
     * - For UPI: Transaction ID from payment app
     * - For Card: Last 4 digits + approval code
     * - For Cheque: Cheque number
     * - For Bank Transfer: NEFT/RTGS/IMPS reference number
     */
    val transactionId: String? = null,

    /**
     * Invoice/bill number from vendor
     * Used for record keeping and GST compliance
     */
    val invoiceNumber: String? = null,

    // ===== GST/TAX =====
    /**
     * GST amount portion of the total amount in INR (₹)
     * Optional - only if GST bill is provided
     * Used for tax calculation and claiming input tax credit
     */
    val gstAmount: Double? = null,

    /**
     * Vendor's GSTIN (GST Identification Number)
     * 15-character alphanumeric code
     * Example: "27AABCU9603R1ZM"
     * Required for registered vendors providing GST invoices
     */
    val vendorGst: String? = null,

    // ===== ADDITIONAL INFO =====
    /**
     * Free-text notes and description
     * Examples:
     * - "50 bags OPC 53 Grade cement @ ₹912 per bag"
     * - "Advance payment - balance ₹25,000 pending"
     * - "Includes 20% discount on bulk purchase"
     */
    val notes: String? = null,

    // ===== RECEIPT/PROOF =====
    /**
     * Cloud storage URL or local file path to receipt photo
     * Full-size image for OCR processing and record keeping
     * May be Google Drive URL, Firebase Storage URL, or local file URI
     */
    val receiptUrl: String? = null,

    /**
     * Thumbnail image URL or path for faster loading in lists
     * Smaller version of receipt for UI display
     * Generated automatically when receipt is uploaded
     */
    val receiptThumbnailUrl: String? = null,

    // ===== SYNC STATUS =====
    /**
     * Sync status flag for offline-first architecture
     * - true: Expense has been synced to cloud storage
     * - false: Expense is only stored locally, pending sync
     *
     * Used to identify records that need to be uploaded when online
     */
    val isSynced: Boolean = false,

    // ===== METADATA =====
    /**
     * Timestamp when expense was created in the app (Unix timestamp in milliseconds)
     * May differ from [date] which is when the actual expense occurred
     */
    val createdDate: Long,

    /**
     * Timestamp of last modification (Unix timestamp in milliseconds)
     * Used for sync conflict resolution and audit trail
     */
    val modifiedDate: Long,

    /**
     * Expense status - maps to [ExpenseStatus] enum
     * Used for soft delete functionality
     * - ACTIVE: Normal, visible expense
     * - DELETED: Soft-deleted, hidden from UI but kept in database
     */
    val status: String // ExpenseStatus enum: ACTIVE or DELETED (soft delete)
)

/**
 * Payment modes commonly used in Indian construction projects
 */
enum class PaymentMode {
    /**
     * Cash payment
     * Direct physical currency exchange
     */
    CASH,

    /**
     * UPI (Unified Payments Interface)
     * Includes PhonePe, GPay, Paytm, BHIM, etc.
     * Most popular digital payment method in India
     */
    UPI,

    /**
     * Credit card payment
     * Swipe/chip/contactless credit card transaction
     */
    CREDIT_CARD,

    /**
     * Debit card payment
     * Swipe/chip/contactless debit card transaction
     */
    DEBIT_CARD,

    /**
     * Cheque payment
     * Bank cheque issued to vendor
     */
    CHEQUE,

    /**
     * Bank transfer
     * NEFT (National Electronic Funds Transfer)
     * RTGS (Real Time Gross Settlement)
     * IMPS (Immediate Payment Service)
     */
    BANK_TRANSFER,

    /**
     * Online payment
     * Net banking or payment gateway transaction
     */
    ONLINE
}

/**
 * Expense record status for soft delete functionality
 */
enum class ExpenseStatus {
    /**
     * Active expense record
     * Normal state - expense is visible and included in all calculations
     */
    ACTIVE,

    /**
     * Soft-deleted expense record
     * Hidden from UI but retained in database for:
     * - Audit trail
     * - Data recovery
     * - Historical reporting
     * - Sync integrity
     */
    DELETED
}
