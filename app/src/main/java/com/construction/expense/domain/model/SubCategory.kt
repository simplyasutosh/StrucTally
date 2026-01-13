package com.construction.expense.domain.model

data class SubCategory(
    val id: String,
    val categoryId: Int,
    val name: String,
    val sortOrder: Int,
    val isCustom: Boolean
)
