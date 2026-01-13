package com.construction.expense.domain.model

data class Category(
    val id: Int,
    val name: String,
    val icon: String,
    val color: String,
    val hasRoomTracking: Boolean,
    val sortOrder: Int,
    val typicalBudgetPercent: String,
    val isCustom: Boolean
)
