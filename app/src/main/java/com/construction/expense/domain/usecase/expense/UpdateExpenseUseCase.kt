package com.construction.expense.domain.usecase.expense

import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.repository.IExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for updating an existing expense.
 *
 * Similar validation to AddExpenseUseCase but updates existing record.
 */
@Singleton
class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository,
    private val addExpenseUseCase: AddExpenseUseCase // Reuse validation logic
) {

    suspend operator fun invoke(expense: Expense): Result<Unit> {
        return try {
            // Validate using same logic as add
            val validationResult = addExpenseUseCase.validateExpense(expense)
            if (validationResult.isNotEmpty()) {
                return Result.failure(
                    ValidationException("Validation failed", validationResult)
                )
            }

            // Update modified timestamp
            val updatedExpense = expense.copy(
                modifiedDate = System.currentTimeMillis(),
                isSynced = false // Mark as not synced
            )

            expenseRepository.updateExpense(updatedExpense)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
