package com.construction.expense.domain.usecase.expense

import com.construction.expense.domain.repository.IExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for deleting an expense.
 *
 * Uses soft delete by default (sets status to DELETED).
 * This keeps audit trail and allows recovery.
 */
@Singleton
class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {

    /**
     * Soft delete an expense
     *
     * @param expenseId Expense ID to delete
     * @param permanent If true, permanently delete. If false, soft delete (default)
     */
    suspend operator fun invoke(
        expenseId: String,
        permanent: Boolean = false
    ): Result<Unit> {
        return try {
            if (permanent) {
                expenseRepository.deleteExpense(expenseId)
            } else {
                expenseRepository.softDeleteExpense(expenseId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Restore a soft-deleted expense
     */
    suspend fun restore(expenseId: String): Result<Unit> {
        return try {
            expenseRepository.restoreExpense(expenseId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
