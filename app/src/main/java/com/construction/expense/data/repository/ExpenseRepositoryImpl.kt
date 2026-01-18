package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.ExpenseDao
import com.construction.expense.data.local.mappers.ExpenseMapper
import com.construction.expense.domain.model.Expense
import com.construction.expense.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val expenseMapper: ExpenseMapper
) : IExpenseRepository {

    override suspend fun createExpense(expense: Expense): Result<String> {
        return try {
            val entity = expenseMapper.toEntity(expense)
            expenseDao.insert(entity)
            Result.success(expense.id)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create expense")
            Result.failure(e)
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            val entity = expenseMapper.toEntity(expense)
            expenseDao.update(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update expense")
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            expenseDao.deleteById(expenseId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete expense")
            Result.failure(e)
        }
    }

    override suspend fun softDeleteExpense(expenseId: String): Result<Unit> {
        return try {
            expenseDao.softDelete(expenseId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to soft delete expense")
            Result.failure(e)
        }
    }

    override suspend fun restoreExpense(expenseId: String): Result<Unit> {
        return try {
            expenseDao.restore(expenseId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to restore expense")
            Result.failure(e)
        }
    }

    override fun getExpenseById(expenseId: String): Flow<Expense?> {
        return expenseDao.getById(expenseId).map { it?.let { expenseMapper.toDomain(it) } }
    }

    override fun getExpensesByProject(projectId: String): Flow<List<Expense>> {
        return expenseDao.getByProject(projectId).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getRecentExpenses(projectId: String, limit: Int): Flow<List<Expense>> {
        return expenseDao.getRecent(projectId, limit).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getExpensesByCategory(projectId: String, categoryId: Int): Flow<List<Expense>> {
        return expenseDao.getByCategory(projectId, categoryId).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getExpensesBySubCategory(projectId: String, subCategoryId: String): Flow<List<Expense>> {
        return expenseDao.getBySubCategory(projectId, subCategoryId).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getExpensesByRoom(projectId: String, roomId: Int): Flow<List<Expense>> {
        return expenseDao.getByRoom(projectId, roomId).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getExpensesByDateRange(
        projectId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Expense>> {
        return expenseDao.getByDateRange(projectId, startDate, endDate).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getExpensesByVendor(projectId: String, vendorName: String): Flow<List<Expense>> {
        return expenseDao.getByVendor(projectId, vendorName).map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun searchExpenses(projectId: String, query: String): Flow<List<Expense>> {
        return expenseDao.search(projectId, "%$query%").map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override fun getTotalByCategory(projectId: String, categoryId: Int): Flow<Double> {
        return expenseDao.getTotalByCategory(projectId, categoryId)
    }

    override fun getUnsyncedExpenses(): Flow<List<Expense>> {
        return expenseDao.getUnsynced().map { entities ->
            entities.map { expenseMapper.toDomain(it) }
        }
    }

    override suspend fun markAsSynced(expenseIds: List<String>): Result<Unit> {
        return try {
            expenseDao.markAsSynced(expenseIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark as synced")
            Result.failure(e)
        }
    }
}
