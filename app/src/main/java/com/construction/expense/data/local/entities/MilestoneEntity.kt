package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing project phases/milestones.
 *
 * Construction projects typically progress through distinct phases.
 * Milestones help track timeline, budget, and progress for each phase.
 *
 * Typical construction phases (in order):
 *
 * 1. Planning & Approvals
 *    - BBMP/municipal approval, building plan approval
 *    - Khata registration, land documents
 *    - Architect plans, structural drawings
 *    - Contractor selection
 *
 * 2. Foundation Work
 *    - Site preparation, excavation
 *    - Footings, plinth beam
 *    - Foundation concrete, waterproofing
 *
 * 3. Structure Work
 *    - Columns, beams, slabs
 *    - Load-bearing walls
 *    - Structural concrete work
 *
 * 4. Roofing
 *    - Roof slab casting
 *    - Waterproofing, terrace treatment
 *    - Drainage setup
 *
 * 5. Plumbing & Electrical (Rough-in)
 *    - Pipe installation in walls
 *    - Electrical conduit, wiring rough-in
 *    - Bathroom drainage points
 *
 * 6. Masonry & Plastering
 *    - Internal wall construction
 *    - Plastering (internal & external)
 *    - Window & door frame installation
 *
 * 7. Flooring & Finishing
 *    - Floor tiles, granite, marble
 *    - Electrical & plumbing fixtures
 *    - Interior painting, woodwork
 *
 * 8. Final Touches
 *    - Landscaping, compound wall
 *    - Final cleanup
 *    - Handover inspection
 *
 * Benefits of milestone tracking:
 * - See which phase is currently active
 * - Track expenses per phase (how much spent on foundation vs finishing)
 * - Monitor timeline adherence (planned vs actual dates)
 * - Identify delays early and document reasons
 * - Budget per phase (allocate ₹10L for foundation, ₹15L for structure, etc.)
 *
 * Note: Milestones are OPTIONAL - users can track expenses without phases.
 */
@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE // Delete milestones when project is deleted
        )
    ],
    indices = [
        Index("projectId") // Fast lookup of milestones for a project
    ]
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Foreign key reference to the project this milestone belongs to
     * Links to [ProjectEntity.id]
     */
    val projectId: String,

    /**
     * Milestone/phase name
     *
     * Examples:
     * - "Planning & Approvals"
     * - "Foundation Work"
     * - "Structure Work"
     * - "Roofing"
     * - "Plumbing & Electrical Rough-in"
     * - "Flooring & Finishing"
     * - "Final Touches"
     *
     * User can create custom phases based on their project needs
     */
    val name: String,

    // ===== TIMELINE TRACKING (all optional) =====

    /**
     * Planned start date for this phase (Unix timestamp in milliseconds)
     * Optional - user's initial timeline estimate
     *
     * Example:
     * - Foundation Work planned to start: 1st January 2024
     */
    val plannedStartDate: Long? = null,

    /**
     * Planned end date for this phase (Unix timestamp in milliseconds)
     * Optional - user's initial timeline estimate
     *
     * Example:
     * - Foundation Work planned to complete: 31st January 2024
     * - Duration: 1 month
     */
    val plannedEndDate: Long? = null,

    /**
     * Actual start date when phase began (Unix timestamp in milliseconds)
     * Optional - recorded when work actually starts
     *
     * Compare with plannedStartDate to identify delays:
     * - Planned: 1st Jan, Actual: 15th Jan → 14 days delayed
     */
    val actualStartDate: Long? = null,

    /**
     * Actual end date when phase completed (Unix timestamp in milliseconds)
     * Optional - recorded when work finishes
     *
     * Compare with plannedEndDate to track completion:
     * - Planned: 31st Jan, Actual: 10th Feb → 10 days delayed
     *
     * Duration = actualEndDate - actualStartDate
     */
    val actualEndDate: Long? = null,

    /**
     * Current status of this milestone - maps to [MilestoneStatus] enum
     * Stored as string for flexibility
     *
     * Status progression:
     * NOT_STARTED → IN_PROGRESS → COMPLETED (or DELAYED)
     */
    val status: String, // MilestoneStatus enum

    /**
     * Optional phase-specific budget in INR (₹)
     *
     * Instead of just project-level budget, allocate per phase:
     * - Foundation Work: ₹10 lakh
     * - Structure Work: ₹15 lakh
     * - Finishing: ₹12 lakh
     *
     * Used for:
     * - Phase-wise budget tracking
     * - Alerts when phase goes over budget
     * - Cash flow planning (when to arrange funds)
     */
    val budget: Double? = null,

    /**
     * Free-text notes about this milestone
     *
     * Common uses:
     * - Delay reasons: "Delayed by 2 weeks due to monsoon rains"
     * - Issues: "Foundation soil required additional treatment"
     * - Achievements: "Completed 5 days ahead of schedule"
     * - Learnings: "Need to order tiles 1 month in advance"
     * - Contractor notes: "Kumar Contractors did excellent work"
     */
    val notes: String? = null
)

/**
 * Enum representing milestone/phase status
 */
enum class MilestoneStatus {
    /**
     * Phase has not started yet
     * Planning stage, waiting for previous phase to complete
     */
    NOT_STARTED,

    /**
     * Phase is currently active
     * Work is in progress, expenses are being recorded
     * Typically only one milestone is IN_PROGRESS at a time
     */
    IN_PROGRESS,

    /**
     * Phase completed successfully
     * Work finished, possibly on time or with acceptable delay
     * actualEndDate should be populated
     */
    COMPLETED,

    /**
     * Phase is behind schedule / significantly delayed
     * Work is in progress but taking longer than planned
     *
     * Triggers:
     * - actualStartDate is significantly after plannedStartDate
     * - Current date > plannedEndDate but phase not complete
     * - User manually marks as delayed
     */
    DELAYED
}
