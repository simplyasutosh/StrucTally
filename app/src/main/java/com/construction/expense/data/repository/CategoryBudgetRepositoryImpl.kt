package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.CategoryBudgetDao
import com.construction.expense.data.local.mappers.CategoryBudgetMapper
import com.construction.expense.domain.model.CategoryBudget
import com.construction.expense.domain.repository.ICategoryBudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryBudgetRepositoryImpl @Inject constructor(
    private val categoryBudgetDao: CategoryBudgetDao,
    private val categoryBudgetMapper: CategoryBudgetMapper
) : ICategoryBudgetRepository {

    override suspend fun setBudget(budget: CategoryBudget): Result<Unit> {
        return try {
            val entity = categoryBudgetMapper.toEntity(budget)
            categoryBudgetDao.upsert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set budget")
            Result.failure(e)
        }
    }

    override suspend fun getBudget(projectId: String, categoryId: Int): CategoryBudget? {
        return try {
            categoryBudgetDao.getBudgetSync(projectId, categoryId)?.let {
                categoryBudgetMapper.toDomain(it)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get budget")
            null
        }
    }

    override fun getAllBudgets(projectId: String): Flow<List<CategoryBudget>> {
        return categoryBudgetDao.getAllBudgets(projectId).map { entities ->
            entities.map { categoryBudgetMapper.toDomain(it) }
        }
    }

    override suspend fun deleteBudget(projectId: String, categoryId: Int): Result<Unit> {
        return try {
            categoryBudgetDao.deleteBudget(projectId, categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete budget")
            Result.failure(e)
        }
    }
}
