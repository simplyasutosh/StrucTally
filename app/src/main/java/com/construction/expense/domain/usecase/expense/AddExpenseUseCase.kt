package com.construction.expense.domain.usecase.expense

import com.construction.expense.domain.model.*
import com.construction.expense.domain.repository.*
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for adding a new expense.
 *
 * This is the most complex use case with multiple responsibilities:
 * 1. Validate expense data
 * 2. Auto-create vendor if doesn't exist
 * 3. Check budget alerts (5 levels)
 * 4. Handle receipt storage
 * 5. Return validation errors and budget warnings
 */
@Singleton
class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository,
    private val vendorRepository: IVendorRepository,
    private val categoryBudgetRepository: ICategoryBudgetRepository,
    private val categoryRepository: ICategoryRepository
) {

    /**
     * Add a new expense
     *
     * @param expense Expense to add
     * @return Result with expense ID on success, or validation errors
     */
    suspend operator fun invoke(expense: Expense): Result<AddExpenseResult> {
        return try {
            // Step 1: Validate expense data
            val validationErrors = validateExpense(expense)
            if (validationErrors.isNotEmpty()) {
                return Result.failure(
                    ValidationException("Validation failed", validationErrors)
                )
            }

            // Step 2: Auto-create vendor if doesn't exist
            ensureVendorExists(expense)

            // Step 3: Check budget alerts
            val budgetAlerts = checkBudgetAlerts(expense)

            // Step 4: Create the expense
            val expenseId = expense.copy(
                id = UUID.randomUUID().toString(),
                createdDate = System.currentTimeMillis(),
                modifiedDate = System.currentTimeMillis(),
                status = ExpenseStatus.ACTIVE,
                isSynced = false
            )

            val result = expenseRepository.createExpense(expenseId)

            if (result.isSuccess) {
                Result.success(
                    AddExpenseResult(
                        expenseId = result.getOrThrow(),
                        budgetAlerts = budgetAlerts
                    )
                )
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Validate expense data
     *
     * Checks:
     * - Amount is positive
     * - Vendor name is not empty
     * - Room is selected (except for Civil Contractor)
     * - Date is not in future
     * - Category and subcategory are valid
     */
    suspend fun validateExpense(expense: Expense): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Validate amount
        if (expense.amount <= 0) {
            errors["amount"] = "Amount must be greater than 0"
        }

        if (expense.amount > 100_000_000) { // 10 crore INR
            errors["amount"] = "Amount seems unrealistically high. Please verify."
        }

        // Validate vendor
        if (expense.vendorName.isBlank()) {
            errors["vendorName"] = "Vendor name is required"
        }

        if (expense.vendorName.length > 100) {
            errors["vendorName"] = "Vendor name is too long (max 100 characters)"
        }

        // Validate room (required for all categories except Civil Contractor)
        val category = categoryRepository.getCategoryById(expense.categoryId).first()
        if (category?.hasRoomTracking == true && expense.roomId == null) {
            errors["roomId"] = "Room selection is required for ${category.name}"
        }

        // Validate date (cannot be in future)
        if (expense.date > System.currentTimeMillis()) {
            errors["date"] = "Expense date cannot be in the future"
        }

        // Validate date (not too old - more than 10 years)
        val tenYearsAgo = System.currentTimeMillis() - (10L * 365 * 24 * 60 * 60 * 1000)
        if (expense.date < tenYearsAgo) {
            errors["date"] = "Expense date seems too old. Please verify."
        }

        // Validate GST
        if (expense.gstAmount != null) {
            if (expense.gstAmount < 0) {
                errors["gstAmount"] = "GST amount cannot be negative"
            }

            // GST should not be more than 28% (highest GST rate in India)
            val gstPercentage = (expense.gstAmount / expense.amount) * 100
            if (gstPercentage > 28) {
                errors["gstAmount"] = "GST seems too high (${gstPercentage.toInt()}%). Please verify."
            }
        }

        // Validate GST number format (if provided)
        if (!expense.vendorGst.isNullOrBlank()) {
            if (!isValidGSTNumber(expense.vendorGst)) {
                errors["vendorGst"] = "Invalid GST number format"
            }
        }

        // Validate notes length
        if (expense.notes != null && expense.notes.length > 1000) {
            errors["notes"] = "Notes are too long (max 1000 characters)"
        }

        return errors
    }

    /**
     * Validate GST number format
     * Format: 22AAAAA0000A1Z5 (15 characters)
     */
    private fun isValidGSTNumber(gstNumber: String): Boolean {
        val gstPattern = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")
        return gstPattern.matches(gstNumber)
    }

    /**
     * Ensure vendor exists, create if not
     *
     * Auto-creates vendor record if this is the first time
     * the vendor name is used in the project.
     */
    private suspend fun ensureVendorExists(expense: Expense) {
        // Check if vendor already exists
        val existingVendor = vendorRepository.getByName(
            expense.projectId,
            expense.vendorName
        )

        if (existingVendor == null) {
            // Create new vendor
            val newVendor = Vendor(
                id = 0, // Auto-generated
                projectId = expense.projectId,
                name = expense.vendorName,
                contact = expense.vendorContact,
                email = null,
                gstNumber = expense.vendorGst,
                address = null,
                notes = "Auto-created from expense",
                createdDate = System.currentTimeMillis()
            )

            vendorRepository.createVendor(newVendor)
        } else {
            // Update vendor if new information provided
            if (!expense.vendorContact.isNullOrBlank() && existingVendor.contact.isNullOrBlank()) {
                vendorRepository.updateVendor(
                    existingVendor.copy(contact = expense.vendorContact)
                )
            }

            if (!expense.vendorGst.isNullOrBlank() && existingVendor.gstNumber.isNullOrBlank()) {
                vendorRepository.updateVendor(
                    existingVendor.copy(gstNumber = expense.vendorGst)
                )
            }
        }
    }

    /**
     * Check budget alerts
     *
     * Calculates budget utilization and returns alerts based on thresholds:
     * - 50% used: LOW alert
     * - 75% used: MEDIUM alert
     * - 90% used: HIGH alert
     * - 100% used: CRITICAL alert
     * - Custom threshold: User-defined alert
     */
    private suspend fun checkBudgetAlerts(expense: Expense): List<BudgetAlert> {
        val alerts = mutableListOf<BudgetAlert>()

        // Get category budget
        val categoryBudget = categoryBudgetRepository.getBudget(
            expense.projectId,
            expense.categoryId
        ) ?: return alerts // No budget set, no alerts

        // Calculate current spending
        val currentSpent = expenseRepository.getTotalByCategory(
            expense.projectId,
            expense.categoryId
        ).first()

        // Calculate new total after this expense
        val newTotal = currentSpent + expense.amount

        // Calculate percentage
        val percentage = if (categoryBudget.budgetedAmount > 0) {
            (newTotal / categoryBudget.budgetedAmount * 100).toInt()
        } else {
            0
        }

        // Get category name for alert messages
        val category = categoryRepository.getCategoryById(expense.categoryId).first()
        val categoryName = category?.name ?: "this category"

        // Check alert thresholds (in order of severity)

        // CRITICAL: 100% or more
        if (percentage >= 100 && categoryBudget.alertThreshold100) {
            alerts.add(
                BudgetAlert(
                    level = BudgetAlertLevel.CRITICAL,
                    title = "Budget Exceeded!",
                    message = "$categoryName budget exceeded! ₹${formatAmount(newTotal)} spent of ₹${formatAmount(categoryBudget.budgetedAmount)} (${percentage}%)",
                    categoryId = expense.categoryId,
                    categoryName = categoryName,
                    budgetedAmount = categoryBudget.budgetedAmount,
                    spentAmount = newTotal,
                    percentage = percentage,
                    shouldBlock = false // Just warn, don't block
                )
            )
        }
        // HIGH: 90-99%
        else if (percentage >= 90 && categoryBudget.alertThreshold90) {
            alerts.add(
                BudgetAlert(
                    level = BudgetAlertLevel.HIGH,
                    title = "Budget Almost Exhausted",
                    message = "$categoryName is at ${percentage}% of budget. Only ₹${formatAmount(categoryBudget.budgetedAmount - newTotal)} remaining.",
                    categoryId = expense.categoryId,
                    categoryName = categoryName,
                    budgetedAmount = categoryBudget.budgetedAmount,
                    spentAmount = newTotal,
                    percentage = percentage,
                    shouldBlock = false
                )
            )
        }
        // MEDIUM: 75-89%
        else if (percentage >= 75 && categoryBudget.alertThreshold75) {
            alerts.add(
                BudgetAlert(
                    level = BudgetAlertLevel.MEDIUM,
                    title = "Budget Warning",
                    message = "$categoryName is at ${percentage}% of budget. ₹${formatAmount(categoryBudget.budgetedAmount - newTotal)} remaining.",
                    categoryId = expense.categoryId,
                    categoryName = categoryName,
                    budgetedAmount = categoryBudget.budgetedAmount,
                    spentAmount = newTotal,
                    percentage = percentage,
                    shouldBlock = false
                )
            )
        }
        // LOW: 50-74%
        else if (percentage >= 50 && categoryBudget.alertThreshold50) {
            alerts.add(
                BudgetAlert(
                    level = BudgetAlertLevel.LOW,
                    title = "Budget Notification",
                    message = "$categoryName is halfway through budget (${percentage}%).",
                    categoryId = expense.categoryId,
                    categoryName = categoryName,
                    budgetedAmount = categoryBudget.budgetedAmount,
                    spentAmount = newTotal,
                    percentage = percentage,
                    shouldBlock = false
                )
            )
        }

        // Check custom threshold
        categoryBudget.customThreshold?.let { customThreshold ->
            if (percentage >= customThreshold && percentage < 100) {
                alerts.add(
                    BudgetAlert(
                        level = BudgetAlertLevel.MEDIUM,
                        title = "Custom Budget Alert",
                        message = "$categoryName reached your custom threshold of ${customThreshold}% (currently at ${percentage}%).",
                        categoryId = expense.categoryId,
                        categoryName = categoryName,
                        budgetedAmount = categoryBudget.budgetedAmount,
                        spentAmount = newTotal,
                        percentage = percentage,
                        shouldBlock = false
                    )
                )
            }
        }

        return alerts
    }

    /**
     * Format amount for display
     */
    private fun formatAmount(amount: Double): String {
        return String.format("%,.2f", amount)
    }
}

/**
 * Result of adding an expense
 */
data class AddExpenseResult(
    val expenseId: String,
    val budgetAlerts: List<BudgetAlert>
)

/**
 * Budget alert data
 */
data class BudgetAlert(
    val level: BudgetAlertLevel,
    val title: String,
    val message: String,
    val categoryId: Int,
    val categoryName: String,
    val budgetedAmount: Double,
    val spentAmount: Double,
    val percentage: Int,
    val shouldBlock: Boolean = false
)

/**
 * Validation exception with field-specific errors
 */
class ValidationException(
    message: String,
    val errors: Map<String, String>
) : Exception(message)
