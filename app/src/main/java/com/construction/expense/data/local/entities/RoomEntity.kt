package com.construction.expense.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing physical rooms/areas in a construction project.
 *
 * Rooms are project-specific - each project has its own set of rooms.
 * When a new project is created, 29 default rooms are automatically added.
 *
 * Default rooms per project:
 *
 * INDOOR (15 rooms):
 * - Living Room, Dining Room, Master Bedroom, Bedroom 2, Bedroom 3
 * - Kitchen, Master Bathroom, Bathroom 2, Common Bathroom
 * - Pooja Room, Study Room, Store Room, Utility Area
 * - Staircase, Entry/Foyer
 *
 * OUTDOOR (10 rooms):
 * - Main Balcony, Bedroom Balcony, Service Balcony, Terrace
 * - Porch, Driveway, Front Garden, Back Garden
 * - Car Parking, Main Gate
 *
 * COMMON (4 areas):
 * - Common Areas (hallways, passages)
 * - Entire House (whole-building expenses)
 * - External/Facade (building exterior)
 * - N/A (when room doesn't apply)
 *
 * Total: 29 default rooms per project
 *
 * IMPORTANT BUSINESS RULE:
 * Room tracking is REQUIRED for all expense categories EXCEPT Civil Contractor (id=1).
 * - Civil work (foundation, structure, columns) applies to entire building → no room needed
 * - All other work (flooring, electrical, plumbing, etc.) → room MUST be specified
 *
 * This rule is enforced by [CategoryEntity.hasRoomTracking] flag.
 */
@Entity(
    tableName = "rooms",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE // Delete rooms when project is deleted
        )
    ],
    indices = [
        Index("projectId"), // Fast lookup of all rooms in a project
        Index(value = ["projectId", "name"], unique = true) // No duplicate room names in same project
    ]
)
data class RoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Foreign key reference to the project this room belongs to
     * Links to [ProjectEntity.id]
     *
     * Rooms are project-specific - deleting a project deletes all its rooms
     */
    val projectId: String,

    /**
     * Room name
     *
     * Examples:
     * - "Living Room"
     * - "Master Bedroom"
     * - "Kitchen"
     * - "Master Bathroom"
     * - "Main Balcony"
     * - "Front Garden"
     *
     * Must be unique within a project (enforced by unique index)
     */
    val name: String,

    /**
     * Room type classification - maps to [RoomType] enum
     * Stored as string for flexibility
     *
     * Used for:
     * - Organizing rooms in UI (Indoor section, Outdoor section)
     * - Filtering (show only indoor rooms)
     * - Reports (indoor vs outdoor spending)
     */
    val type: String, // RoomType enum: INDOOR, OUTDOOR, COMMON

    // ===== OPTIONAL DETAILS =====

    /**
     * Floor level where this room is located
     * Optional - useful for multi-story buildings
     *
     * Common values:
     * - "GROUND" - Ground floor
     * - "FIRST" - First floor
     * - "SECOND" - Second floor
     * - "TERRACE" - Rooftop/terrace
     * - "BASEMENT" - Below ground level
     *
     * Helps with:
     * - Floor-wise cost analysis
     * - Material transportation planning (stairs vs elevator)
     * - Contractor scheduling
     */
    val floorLevel: String? = null,

    /**
     * Room area in square feet
     * Optional - for cost per square foot calculations
     *
     * Examples:
     * - Living Room: 250.0 sqft
     * - Master Bedroom: 180.0 sqft
     * - Kitchen: 120.0 sqft
     *
     * Used for:
     * - Cost per sqft analysis (flooring cost per sqft)
     * - Budget estimation based on area
     * - Comparing costs across rooms of different sizes
     */
    val squareFootage: Double? = null,

    /**
     * Optional room-specific budget in INR (₹)
     *
     * Some users prefer to allocate budget per room instead of per category:
     * - Master Bedroom total budget: ₹5 lakh
     * - Kitchen total budget: ₹8 lakh
     * - Living Room total budget: ₹6 lakh
     *
     * Used for:
     * - Room-wise budget tracking
     * - Alerts when a room goes over budget
     * - Prioritizing spending across rooms
     */
    val budget: Double? = null,

    /**
     * Active status flag
     * - true: Room is active and shown in expense forms
     * - false: Room is deactivated/hidden
     *
     * Use cases:
     * - User planned a "Study Room" but later merged it with "Bedroom 3"
     * - "Guest Room" not built in Phase 1, deactivated until Phase 2
     * - Keeps data integrity while hiding unused rooms
     *
     * Deactivated rooms:
     * - Not shown in expense dropdowns
     * - Still retain historical expenses
     * - Can be reactivated later
     */
    val isActive: Boolean = true,

    /**
     * Display order in UI lists
     * Lower numbers appear first
     *
     * Typical ordering:
     * - Living areas (1-10)
     * - Bedrooms (11-20)
     * - Bathrooms (21-30)
     * - Outdoor areas (31-40)
     * - Common areas (41+)
     *
     * Users can customize order based on preference
     */
    val sortOrder: Int,

    /**
     * Timestamp when room was created (Unix timestamp in milliseconds)
     * Used for audit trail and sync operations
     */
    val createdDate: Long
)

/**
 * Enum representing room type classifications
 */
enum class RoomType {
    /**
     * Indoor rooms - inside the house
     * Examples: Living Room, Bedrooms, Kitchen, Bathrooms
     */
    INDOOR,

    /**
     * Outdoor areas - outside the house but within property
     * Examples: Balconies, Garden, Driveway, Parking
     */
    OUTDOOR,

    /**
     * Common or general areas
     * Examples:
     * - "Common Areas" - hallways, passages
     * - "Entire House" - whole-building expenses
     * - "External/Facade" - building exterior
     * - "N/A" - when room doesn't apply
     */
    COMMON
}
