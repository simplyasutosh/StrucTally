package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a construction project.
 *
 * A project is the top-level container for all expenses. Examples:
 * - "Main House Construction"
 * - "Kitchen Renovation"
 * - "Garden Landscaping"
 *
 * Each project tracks its own budget, timeline, expenses, and team.
 */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String, // UUID string

    // ===== BASIC INFORMATION =====
    /**
     * Project name (e.g., "Main House Construction")
     */
    val name: String,

    /**
     * Type of project - maps to [ProjectType] enum
     * Stored as string for flexibility and Room compatibility
     */
    val type: String, // ProjectType enum: NEW_CONSTRUCTION, RENOVATION, ADDITION, INTERIOR, OTHER

    /**
     * Project address/location
     */
    val location: String,

    /**
     * Project start date as Unix timestamp in milliseconds
     */
    val startDate: Long,

    /**
     * Expected/planned completion date as Unix timestamp in milliseconds
     */
    val expectedEndDate: Long,

    /**
     * Actual completion date as Unix timestamp in milliseconds
     * Null if project is still ongoing
     */
    val actualEndDate: Long? = null,

    /**
     * Current project lifecycle status - maps to [ProjectStatus] enum
     * Stored as string for flexibility and Room compatibility
     */
    val status: String, // ProjectStatus enum: PLANNING, ACTIVE, ON_HOLD, COMPLETED, ABANDONED

    // ===== FINANCIAL DETAILS =====
    /**
     * Cost of land in INR (₹)
     * Optional - may not apply to renovations or interior projects
     */
    val landCost: Double? = null,

    /**
     * Total project budget in INR (₹)
     * This is the overall budget allocation for the entire project
     */
    val totalBudget: Double,

    /**
     * Home loan/mortgage amount in INR (₹)
     * Optional - only if project is financed through a loan
     */
    val loanAmount: Double? = null,

    /**
     * Name of lending institution (bank/NBFC)
     * Optional - only applicable if loan is taken
     */
    val bankName: String? = null,

    /**
     * Annual interest rate percentage (e.g., 8.5 for 8.5%)
     * Optional - only applicable if loan is taken
     */
    val interestRate: Double? = null,

    /**
     * Monthly EMI (Equated Monthly Installment) amount in INR (₹)
     * Optional - only applicable if loan is taken
     */
    val emiAmount: Double? = null,

    // ===== PEOPLE & CONTACTS =====
    /**
     * Primary project owner name
     * The main person responsible for the project
     */
    val ownerName: String,

    /**
     * Co-owner name (spouse/partner/family member)
     * Optional - for jointly owned projects
     */
    val coOwnerName: String? = null,

    /**
     * Main contractor name
     * The primary contractor managing the construction
     * Optional - may be self-managed or not yet assigned
     */
    val contractorName: String? = null,

    /**
     * Main contractor phone number
     * Optional - for quick contact
     */
    val contractorContact: String? = null,

    /**
     * Architect name
     * Optional - not all projects have an architect
     */
    val architectName: String? = null,

    /**
     * Architect phone number
     * Optional - for quick contact
     */
    val architectContact: String? = null,

    // ===== METADATA =====
    /**
     * Timestamp when project was created in the app (Unix timestamp in milliseconds)
     */
    val createdDate: Long,

    /**
     * Timestamp of last modification to project details (Unix timestamp in milliseconds)
     * Used for sync conflict resolution
     */
    val modifiedDate: Long
)

/**
 * Enum representing different types of construction projects
 */
enum class ProjectType {
    /**
     * Building a new structure from scratch
     * Includes foundation, structure, and all construction phases
     */
    NEW_CONSTRUCTION,

    /**
     * Remodeling or renovating an existing structure
     * May include demolition, reconstruction, or upgrades
     */
    RENOVATION,

    /**
     * Adding new rooms, floors, or extensions to existing structure
     */
    ADDITION,

    /**
     * Interior design and furnishing only
     * No structural or civil work involved
     */
    INTERIOR,

    /**
     * User-defined or miscellaneous project types
     */
    OTHER
}

/**
 * Enum representing the lifecycle status of a construction project
 */
enum class ProjectStatus {
    /**
     * Planning phase - project not yet started
     * Includes budgeting, design, and approvals
     */
    PLANNING,

    /**
     * Construction is currently in progress
     * This is the main active state of the project
     */
    ACTIVE,

    /**
     * Project is temporarily paused or on hold
     * May be due to funding, weather, approvals, etc.
     */
    ON_HOLD,

    /**
     * Project has been successfully completed
     * All work finished and project closed
     */
    COMPLETED,

    /**
     * Project was cancelled or abandoned
     * Work stopped permanently before completion
     */
    ABANDONED
}
