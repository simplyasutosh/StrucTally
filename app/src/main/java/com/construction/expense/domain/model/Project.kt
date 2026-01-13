package com.construction.expense.domain.model

/**
 * Domain model for a construction project.
 *
 * This is a pure Kotlin data class (POJO) with no Android/Room dependencies.
 * It includes computed properties that aren't stored in database.
 */
data class Project(
    val id: String,

    // Basic Info
    val name: String,
    val type: ProjectType,
    val location: String,
    val startDate: Long,
    val expectedEndDate: Long,
    val actualEndDate: Long? = null,
    val status: ProjectStatus,

    // Financial
    val landCost: Double? = null,
    val totalBudget: Double,
    val loanAmount: Double? = null,
    val bankName: String? = null,
    val interestRate: Double? = null,
    val emiAmount: Double? = null,

    // People
    val ownerName: String,
    val coOwnerName: String? = null,
    val contractorName: String? = null,
    val contractorContact: String? = null,
    val architectName: String? = null,
    val architectContact: String? = null,

    // Metadata
    val createdDate: Long,
    val modifiedDate: Long,

    // ===== COMPUTED PROPERTIES (not in database) =====
    val totalExpenses: Double = 0.0,
    val expenseCount: Int = 0,
    val budgetRemaining: Double = totalBudget - totalExpenses,
    val budgetUtilization: Double = if (totalBudget > 0) (totalExpenses / totalBudget * 100) else 0.0,
    val isOverBudget: Boolean = totalExpenses > totalBudget,
    val durationInDays: Long = ((actualEndDate ?: System.currentTimeMillis()) - startDate) / (1000 * 60 * 60 * 24)
) {
    /**
     * Formatted budget utilization for display
     */
    fun getFormattedUtilization(): String {
        return "${budgetUtilization.toInt()}%"
    }

    /**
     * Status emoji for UI
     */
    fun getStatusEmoji(): String {
        return when (status) {
            ProjectStatus.PLANNING -> "📋"
            ProjectStatus.ACTIVE -> "🏗️"
            ProjectStatus.ON_HOLD -> "⏸️"
            ProjectStatus.COMPLETED -> "✅"
            ProjectStatus.ABANDONED -> "❌"
        }
    }
}

enum class ProjectType {
    NEW_CONSTRUCTION,
    RENOVATION,
    ADDITION,
    INTERIOR,
    OTHER
}

enum class ProjectStatus {
    PLANNING,
    ACTIVE,
    ON_HOLD,
    COMPLETED,
    ABANDONED
}
