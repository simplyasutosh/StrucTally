package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for vendor/supplier information.
 *
 * Vendors are contractors and suppliers you pay during construction:
 *
 * Material Suppliers:
 * - "Shree Cement Suppliers" - cement, sand, aggregates
 * - "Tile World" - tiles, granite, marble
 * - "Steel & Iron Traders" - TMT bars, steel
 * - "Plywood Palace" - plywood, veneer, laminates
 *
 * Contractors:
 * - "Ramesh Masonry Works" - civil contractor
 * - "Kumar Electricals" - electrical contractor
 * - "Sharma Plumbing Services" - plumbing contractor
 * - "Singh Painting Works" - painting contractor
 *
 * Service Providers:
 * - "Architect Sharma & Associates" - architectural services
 * - "Inspection Services Ltd" - quality inspections
 * - "Legal Consultants" - documentation, approvals
 *
 * How vendor tracking works:
 *
 * 1. AUTOMATIC CREATION:
 *    When you add an expense, enter vendor name "Shree Cement Suppliers"
 *    → App automatically creates a VendorEntity record
 *    → Subsequent expenses can select from existing vendors
 *
 * 2. AUTO-SUGGESTIONS:
 *    When typing vendor name, app suggests:
 *    - Previously used vendors
 *    - Vendors with similar names
 *    - Contact details are pre-filled
 *
 * 3. VENDOR DIRECTORY:
 *    App maintains a directory of all vendors:
 *    - Contact information
 *    - Total amount paid to each vendor
 *    - Number of transactions
 *    - GST details for tax purposes
 *
 * 4. REPORTS:
 *    - Vendor-wise spending report (how much paid to each vendor)
 *    - Identify top 5 vendors by spending
 *    - Payment history per vendor
 *
 * Benefits:
 * - No need to retype vendor names and contact details
 * - Track total spending per vendor for negotiation
 * - Maintain vendor contact directory in one place
 * - GST compliance tracking for tax filing
 * - Quality tracking via notes (good/bad experiences)
 */
@Entity(
    tableName = "vendors",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE // Delete vendors when project is deleted
        )
    ],
    indices = [
        Index("projectId"), // Fast lookup of vendors for a project
        Index("name"), // Fast vendor name search for auto-suggestions
        Index(value = ["projectId", "name"], unique = true) // No duplicate vendor names per project
    ]
)
data class VendorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Foreign key reference to the project
     * Links to [ProjectEntity.id]
     *
     * Vendors are project-specific:
     * - Same vendor name can exist in different projects
     * - Different contact details per project allowed
     * - Enables separate vendor tracking per project
     */
    val projectId: String,

    /**
     * Vendor/supplier name
     *
     * Examples:
     * - "Shree Cement Suppliers"
     * - "Tile World - MG Road Branch"
     * - "Ramesh Masonry Works"
     * - "Kumar Electricals"
     * - "Architect Sharma & Associates"
     *
     * Must be unique within a project (enforced by unique index)
     * Case-sensitive to preserve user's preferred capitalization
     */
    val name: String,

    // ===== CONTACT INFORMATION (all optional) =====

    /**
     * Vendor phone number
     * Optional - for quick contact and reorders
     *
     * Format: Any (app doesn't enforce format)
     * Common formats in India:
     * - "9876543210" (10-digit mobile)
     * - "+91 98765 43210" (with country code)
     * - "080-12345678" (landline with STD code)
     *
     * Used for:
     * - Click-to-call from app
     * - Reordering materials
     * - Emergency contact during construction
     */
    val contact: String? = null,

    /**
     * Vendor email address
     * Optional - for digital communication and invoices
     *
     * Examples:
     * - "sales@shreecementsuppliers.com"
     * - "ramesh.masonry@gmail.com"
     *
     * Used for:
     * - Request quotations
     * - Email invoices and receipts
     * - Send payment confirmations
     */
    val email: String? = null,

    /**
     * Vendor's GSTIN (GST Identification Number)
     * 15-character alphanumeric code
     *
     * Format: 2 digits (state) + 10 digits (PAN) + 1 digit (entity) + 1 letter (Z) + 1 check digit
     * Example: "27AABCU9603R1ZM"
     *
     * Required for:
     * - Registered vendors providing GST invoices
     * - Claiming input tax credit (ITC)
     * - Tax compliance and filing
     * - Businesses with turnover > ₹20 lakh (₹40 lakh for services)
     *
     * Optional because:
     * - Small vendors may not be GST registered
     * - Labor contractors often unregistered
     * - Cash transactions < ₹50,000 may not require GST
     */
    val gstNumber: String? = null,

    /**
     * Vendor business address
     * Optional - useful for visiting shops, offices, warehouses
     *
     * Examples:
     * - "123, MG Road, Bangalore - 560001"
     * - "Industrial Area, Phase 2, Peenya"
     *
     * Used for:
     * - Navigation to vendor location
     * - Identifying branch (if vendor has multiple locations)
     * - Delivery coordination
     */
    val address: String? = null,

    /**
     * Free-text notes about vendor
     *
     * Common uses:
     *
     * QUALITY RATINGS:
     * - "⭐⭐⭐⭐⭐ Excellent quality materials, on-time delivery"
     * - "⭐⭐⭐ Average quality, but cheapest in area"
     * - "Reliable contractor, completed work ahead of schedule"
     *
     * PREFERENCES:
     * - "Always give 10% discount on bulk orders"
     * - "Best prices for tiles - check before buying elsewhere"
     * - "Delivery takes 2-3 days, plan accordingly"
     *
     * ISSUES:
     * - "Delivered wrong items twice, check carefully"
     * - "Poor customer service, avoid if possible"
     * - "Cash only - no UPI/card accepted"
     *
     * RECOMMENDATIONS:
     * - "Recommended by architect"
     * - "Used by neighbor for their construction"
     *
     * Helps with:
     * - Future decision making (reuse good vendors)
     * - Avoiding problematic vendors
     * - Negotiations and pricing references
     */
    val notes: String? = null,

    /**
     * Timestamp when vendor was first added (Unix timestamp in milliseconds)
     * Automatically set when vendor record is created
     *
     * Used for:
     * - Sorting vendors (show recent vendors first)
     * - Audit trail
     * - Sync operations
     */
    val createdDate: Long
)
