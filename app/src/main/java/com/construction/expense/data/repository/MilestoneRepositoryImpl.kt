package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.MilestoneDao
import com.construction.expense.data.local.mappers.MilestoneMapper
import com.construction.expense.domain.model.Milestone
import com.construction.expense.domain.repository.IMilestoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneRepositoryImpl @Inject constructor(
    private val milestoneDao: MilestoneDao,
    private val milestoneMapper: MilestoneMapper
) : IMilestoneRepository {

    override suspend fun createMilestone(milestone: Milestone): Result<Int> {
        return try {
            val entity = milestoneMapper.toEntity(milestone)
            val id = milestoneDao.insert(entity).toInt()
            Result.success(id)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create milestone")
            Result.failure(e)
        }
    }

    override suspend fun updateMilestone(milestone: Milestone): Result<Unit> {
        return try {
            val entity = milestoneMapper.toEntity(milestone)
            milestoneDao.update(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update milestone")
            Result.failure(e)
        }
    }

    override suspend fun deleteMilestone(milestoneId: Int): Result<Unit> {
        return try {
            milestoneDao.deleteById(milestoneId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete milestone")
            Result.failure(e)
        }
    }

    override fun getMilestoneById(milestoneId: Int): Flow<Milestone?> {
        return milestoneDao.getById(milestoneId).map { it?.let { milestoneMapper.toDomain(it) } }
    }

    override fun getMilestonesByProject(projectId: String): Flow<List<Milestone>> {
        return milestoneDao.getByProject(projectId).map { entities ->
            entities.map { milestoneMapper.toDomain(it) }
        }
    }

    override fun getActiveMilestonesByProject(projectId: String): Flow<List<Milestone>> {
        return milestoneDao.getActiveByProject(projectId).map { entities ->
            entities.map { milestoneMapper.toDomain(it) }
        }
    }
}
