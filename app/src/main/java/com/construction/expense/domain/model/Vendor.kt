package com.construction.expense.domain.model

data class Vendor(
    val id: Int,
    val projectId: String,
    val name: String,
    val contact: String? = null,
    val email: String? = null,
    val gstNumber: String? = null,
    val address: String? = null,
    val notes: String? = null,
    val createdDate: Long
)
