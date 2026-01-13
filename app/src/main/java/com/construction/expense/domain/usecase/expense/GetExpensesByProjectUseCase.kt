package com.construction.expense.domain.usecase.expense

import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting all expenses for a project.
 */
@Singleton
class GetExpensesByProjectUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {

    operator fun invoke(projectId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByProject(projectId)
    }

    /**
     * Get recent expenses with limit
     */
    fun getRecent(projectId: String, limit: Int = 10): Flow<List<Expense>> {
        return expenseRepository.getRecentExpenses(projectId, limit)
    }
}
