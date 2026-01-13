package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an expense category.
 *
 * Categories follow real-world contractor structure in Indian construction:
 * 1. Civil Contractor (structure, foundation, masonry)
 * 2. Flooring Contractor (tiles, marble, granite)
 * 3. Electrical Contractor (wiring, switches, outlets)
 * 4. Electrical Items (fans, lights, appliances)
 * 5. Plumbing Contractor (pipes, fittings, installation)
 * 6. Windows & Doors (frames, shutters, hardware)
 * 7. Painting Contractor (interior/exterior painting)
 * 8. Carpentry & Woodwork (cabinets, wardrobes, ceiling)
 * 9. Interior Design (furniture, furnishing, decor)
 * 10. Gardening & Landscaping (lawn, plants, features)
 * 11. Specialized Work (lift, solar, security systems)
 * 12. Services & Miscellaneous (fees, documentation, rituals)
 *
 * Total: 12 default categories, prepopulated on first app launch.
 * Users can optionally add custom categories with IDs > 12.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Int, // 1-12 for default categories, >12 for custom categories

    /**
     * Category display name
     * Examples:
     * - "Civil Contractor"
     * - "Flooring Contractor"
     * - "Electrical Contractor"
     * - "Plumbing Contractor"
     */
    val name: String,

    /**
     * Emoji icon for visual representation in UI
     * Examples:
     * - "🏗️" for Civil Contractor
     * - "🔲" for Flooring Contractor
     * - "⚡" for Electrical Contractor
     * - "🚰" for Plumbing Contractor
     * - "🚪" for Windows & Doors
     * - "🎨" for Painting Contractor
     * - "🪵" for Carpentry & Woodwork
     * - "🛋️" for Interior Design
     * - "🌳" for Gardening & Landscaping
     * - "🔧" for Specialized Work
     * - "📄" for Services & Miscellaneous
     */
    val icon: String,

    /**
     * Hex color code for UI theming and visual distinction
     * Each category has a unique color for:
     * - Chart visualization
     * - List item backgrounds
     * - Budget progress bars
     *
     * Examples:
     * - "#FF6B6B" for Civil (red-ish)
     * - "#4ECDC4" for Flooring (teal)
     * - "#FFD93D" for Electrical (yellow)
     * - "#6BCF7F" for Plumbing (green)
     */
    val color: String,

    /**
     * Room tracking requirement flag
     *
     * Determines whether expenses in this category must specify a room/area:
     *
     * FALSE (hasRoomTracking = false):
     * - Civil Contractor (id=1): Structure work applies to entire building
     *   Examples: Foundation, columns, beams, walls
     *   These don't belong to a specific room
     *
     * TRUE (hasRoomTracking = true):
     * - All other categories (id=2-12): Must specify which room
     *   Examples:
     *   - Flooring: Living room tiles, bedroom flooring
     *   - Electrical: Kitchen wiring, bedroom switches
     *   - Plumbing: Bathroom fittings, kitchen sink
     *
     * Used for validation and UI logic when adding expenses
     */
    val hasRoomTracking: Boolean,

    /**
     * Display order in lists and menus (1-12 for defaults)
     * Categories are displayed in this order throughout the app:
     * - Add expense category selector
     * - Dashboard category breakdown
     * - Reports and charts
     *
     * Lower numbers appear first (Civil Contractor = 1 is shown first)
     */
    val sortOrder: Int,

    /**
     * Typical budget allocation percentage range for guidance
     * Based on industry standards for Indian residential construction
     *
     * Examples:
     * - Civil Contractor: "35-45%" (largest allocation)
     * - Flooring: "8-12%"
     * - Electrical Contractor: "6-8%"
     * - Plumbing: "5-7%"
     * - Interior Design: "10-15%"
     *
     * Displayed as guidance when user sets up project budgets
     * Format: "X-Y%" where X and Y are percentages
     */
    val typicalBudgetPercent: String,

    /**
     * Flag indicating if this is a custom user-created category
     *
     * - false: Default category (id 1-12), prepopulated by app
     *   Cannot be deleted or significantly modified
     *
     * - true: Custom category added by user (id > 12)
     *   Can be edited or deleted by user
     *   Useful for specialized projects with unique requirements
     */
    val isCustom: Boolean
)
