package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing expense subcategories.
 *
 * Each main category has multiple subcategories for detailed expense tracking.
 *
 * Example - Category 2 (Flooring Contractor) has 8 subcategories:
 * - 2.1: Living Areas Flooring
 * - 2.2: Kitchen Flooring
 * - 2.3: Bathroom Flooring
 * - 2.4: Balcony & Utility Flooring
 * - 2.5: Staircase & Landing
 * - 2.6: Outdoor Flooring
 * - 2.7: Flooring Labor
 * - 2.8: Skirting & Finishing
 *
 * Total: 80 subcategories across 12 main categories (prepopulated).
 *
 * ID format: "CategoryId.SubNumber" (e.g., "2.1", "2.2", "10.10")
 * This format allows easy identification of parent category and sorting.
 *
 * Benefits of subcategories:
 * - Detailed expense breakdown (not just "Flooring" but "Kitchen Flooring")
 * - Better budget analysis (compare living room flooring vs bedroom flooring)
 * - Helps identify cost patterns (is bathroom flooring always expensive?)
 * - Professional reporting aligned with contractor invoices
 */
@Entity(
    tableName = "sub_categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE // Delete subcategories when parent category is deleted
        )
    ],
    indices = [
        Index("categoryId") // Fast lookup of subcategories by category
    ]
)
data class SubCategoryEntity(
    @PrimaryKey
    val id: String, // Format: "X.Y" (e.g., "1.1", "2.5", "10.10")

    /**
     * Foreign key reference to parent category
     * Links to [CategoryEntity.id] (1-12 for default categories)
     *
     * Example:
     * - categoryId = 2 means this subcategory belongs to "Flooring Contractor"
     * - categoryId = 3 means this belongs to "Electrical Contractor"
     */
    val categoryId: Int,

    /**
     * Subcategory display name
     *
     * Examples:
     * - "Living Areas Flooring"
     * - "Kitchen Flooring"
     * - "Master Bedroom Electrical"
     * - "Common Bathroom Plumbing"
     * - "Interior Painting"
     */
    val name: String,

    /**
     * Display order within the parent category
     * Lower numbers appear first in UI lists
     *
     * Example for Flooring (Category 2):
     * - sortOrder = 1: Living Areas Flooring (2.1)
     * - sortOrder = 2: Kitchen Flooring (2.2)
     * - sortOrder = 3: Bathroom Flooring (2.3)
     * - etc.
     */
    val sortOrder: Int,

    /**
     * Flag indicating if this is a custom user-created subcategory
     *
     * - false: Default subcategory (part of the 80 prepopulated ones)
     *   Cannot be deleted, only deactivated
     *
     * - true: Custom subcategory added by user for specific needs
     *   Can be edited or deleted by user
     *
     * Use case for custom subcategories:
     * - User has a "Home Theater Room" and wants specific subcategory
     * - Special contractor not covered by defaults
     * - Unique project requirements
     */
    val isCustom: Boolean = false
)
