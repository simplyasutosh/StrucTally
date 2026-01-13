package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for category-wise budget allocation.
 *
 * Instead of just tracking total project budget, this enables detailed
 * budget allocation across the 12 expense categories.
 *
 * Example: ₹50 lakh project budget breakdown:
 *
 * 1. Civil Contractor: ₹22,00,000 (44%)
 *    - Foundation, structure, masonry, plastering
 *    - Typically the largest allocation
 *
 * 2. Flooring Contractor: ₹5,00,000 (10%)
 *    - Tiles, granite, marble, labor
 *
 * 3. Electrical Contractor: ₹3,50,000 (7%)
 *    - Wiring, conduits, switchboards, labor
 *
 * 4. Electrical Items: ₹3,00,000 (6%)
 *    - Fans, lights, appliances, switches
 *
 * 5. Plumbing Contractor: ₹3,00,000 (6%)
 *    - Pipes, fittings, installation labor
 *
 * 6. Windows & Doors: ₹4,50,000 (9%)
 *    - Frames, shutters, hardware
 *
 * 7. Painting Contractor: ₹3,00,000 (6%)
 *    - Interior & exterior painting, labor
 *
 * 8. Carpentry & Woodwork: ₹6,00,000 (12%)
 *    - Kitchen cabinets, wardrobes, false ceiling
 *
 * 9. Interior Design: ₹10,00,000 (20%)
 *    - Furniture, furnishing, decor
 *    - Often the second-largest allocation
 *
 * 10. Gardening & Landscaping: ₹2,00,000 (4%)
 *     - Lawn, plants, garden features
 *
 * 11. Specialized Work: ₹1,50,000 (3%)
 *     - Lift, solar panels, security systems
 *
 * 12. Services & Miscellaneous: ₹2,50,000 (5%)
 *     - Architect fees, approvals, documentation
 *
 * Total: ₹50,00,000 (100%)
 *
 * BUDGET ALERT SYSTEM:
 *
 * The app monitors spending vs budget and alerts at thresholds:
 *
 * 50% THRESHOLD (Informational 🟢):
 * - "You've used ₹2.5L of ₹5L flooring budget (50%)"
 * - "Halfway through your flooring budget"
 * - Keep user informed without urgency
 *
 * 75% THRESHOLD (Warning 🟡):
 * - "Flooring expenses at ₹3.75L - 75% of ₹5L budget"
 * - "Only ₹1.25L remaining in flooring budget"
 * - Time to start monitoring more carefully
 *
 * 90% THRESHOLD (Urgent 🟠):
 * - "ALERT: Flooring at ₹4.5L - 90% of budget used!"
 * - "Only ₹50k remaining"
 * - Immediate attention needed
 *
 * 100% THRESHOLD (Critical 🔴):
 * - "BUDGET EXCEEDED: Flooring at ₹5.2L (₹20k over budget)"
 * - "Consider reallocating from other categories"
 * - Budget overrun occurred
 *
 * CUSTOM THRESHOLD:
 * - User can set custom alerts (e.g., "Alert me at 85%")
 * - Flexible based on user's monitoring preference
 *
 * Benefits of category budgets:
 * - Early warning system - catch overruns before project goes over budget
 * - Identify problem categories (civil work constantly over budget?)
 * - Reallocate funds between categories if needed
 * - Better financial planning and cash flow management
 * - Professional reporting aligned with industry standards
 */
@Entity(
    tableName = "category_budgets",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE // Delete category budgets when project is deleted
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE // Delete budget if category is deleted (rare)
        )
    ],
    indices = [
        Index("projectId"), // Fast lookup of all category budgets for a project
        Index("categoryId"), // Fast lookup by category
        Index(value = ["projectId", "categoryId"], unique = true) // One budget entry per category per project
    ]
)
data class CategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Foreign key reference to the project
     * Links to [ProjectEntity.id]
     */
    val projectId: String,

    /**
     * Foreign key reference to the category
     * Links to [CategoryEntity.id] (1-12 for default categories)
     *
     * Each project can have up to 12 category budget entries
     * (one for each main category)
     */
    val categoryId: Int, // 1-12

    /**
     * Allocated budget amount for this category in INR (₹)
     *
     * Examples:
     * - Civil Contractor: 22,00,000.0 (₹22 lakh)
     * - Flooring: 5,00,000.0 (₹5 lakh)
     * - Interior Design: 10,00,000.0 (₹10 lakh)
     *
     * Sum of all category budgets should equal [ProjectEntity.totalBudget]
     * (though app doesn't strictly enforce this - users have flexibility)
     */
    val budgetedAmount: Double,

    // ===== ALERT THRESHOLD CONFIGURATION =====
    // Each threshold can be independently enabled/disabled

    /**
     * Enable alert at 50% budget utilization
     * Default: true (informational alert enabled)
     *
     * When triggered:
     * - Spending reaches 50% of budgetedAmount
     * - Example: ₹2.5L spent of ₹5L budget
     *
     * Alert type: Informational 🟢
     * - Not urgent, just keeping user informed
     * - "Halfway through flooring budget"
     */
    val alertThreshold50: Boolean = true,

    /**
     * Enable alert at 75% budget utilization
     * Default: true (warning alert enabled)
     *
     * When triggered:
     * - Spending reaches 75% of budgetedAmount
     * - Example: ₹3.75L spent of ₹5L budget
     *
     * Alert type: Warning 🟡
     * - Start monitoring more carefully
     * - "Flooring at 75% of budget - ₹1.25L remaining"
     */
    val alertThreshold75: Boolean = true,

    /**
     * Enable alert at 90% budget utilization
     * Default: true (urgent alert enabled)
     *
     * When triggered:
     * - Spending reaches 90% of budgetedAmount
     * - Example: ₹4.5L spent of ₹5L budget
     *
     * Alert type: Urgent 🟠
     * - Immediate attention needed
     * - "ALERT: Flooring at 90% - only ₹50k remaining"
     * - Consider stopping or reallocating
     */
    val alertThreshold90: Boolean = true,

    /**
     * Enable alert at 100% budget utilization (budget exceeded)
     * Default: true (critical alert enabled)
     *
     * When triggered:
     * - Spending reaches or exceeds 100% of budgetedAmount
     * - Example: ₹5.2L spent of ₹5L budget (₹20k over)
     *
     * Alert type: Critical 🔴
     * - Budget overrun occurred
     * - "BUDGET EXCEEDED: Flooring ₹20k over budget"
     * - Need to reallocate from other categories or increase total budget
     */
    val alertThreshold100: Boolean = true,

    /**
     * Custom threshold percentage (optional)
     * User-defined alert level between 0-100
     *
     * Examples:
     * - customThreshold = 85 → Alert at 85% budget usage
     * - customThreshold = 60 → Alert at 60% budget usage
     * - customThreshold = null → No custom alert
     *
     * Use cases:
     * - Conservative users: Set to 60% or 70% for early warning
     * - Flexible categories: Set to 95% for less critical categories
     * - Tight budgets: Set to 80% to monitor closely
     *
     * When triggered:
     * - Spending reaches customThreshold% of budgetedAmount
     * - Example: customThreshold=85, spending at ₹4.25L of ₹5L budget
     * - Alert: "Custom alert: Flooring at 85% of budget"
     */
    val customThreshold: Int? = null, // Optional: 0-100

    /**
     * Timestamp of last modification (Unix timestamp in milliseconds)
     *
     * Updated when:
     * - Budget amount is changed
     * - Alert thresholds are modified
     *
     * Used for:
     * - Audit trail (when was budget last adjusted?)
     * - Sync conflict resolution
     * - Budget revision history
     */
    val modifiedDate: Long
)
