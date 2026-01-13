package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.ExpenseDao
import com.construction.expense.domain.model.*
import com.construction.expense.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : IReportRepository {

    override fun getExpenseSummary(projectId: String): Flow<ExpenseSummary> = flow {
        // TODO: Implement with complex queries
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getCategoryExpenseSummary(projectId: String): Flow<List<CategoryExpenseSummary>> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getCategoryExpenseSummaryById(projectId: String, categoryId: Int): Flow<CategoryExpenseSummary?> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getRoomExpenseSummary(projectId: String): Flow<List<RoomExpenseSummary>> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getRoomExpenseSummaryById(projectId: String, roomId: Int): Flow<RoomExpenseSummary?> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getMonthlyExpenseSummary(projectId: String, months: Int): Flow<List<MonthlyExpense>> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getVendorExpenseSummary(projectId: String): Flow<List<VendorExpenseSummary>> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }

    override fun getVendorExpenseSummaryByName(projectId: String, vendorName: String): Flow<VendorExpenseSummary?> = flow {
        // TODO: Implement
        throw NotImplementedError("Report repository not yet implemented")
    }
}
