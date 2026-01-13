package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.ProjectDao
import com.construction.expense.data.local.dao.ExpenseDao
import com.construction.expense.data.local.mappers.ProjectMapper
import com.construction.expense.domain.model.Project
import com.construction.expense.domain.model.ProjectWithSummary
import com.construction.expense.domain.repository.IProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val expenseDao: ExpenseDao,
    private val projectMapper: ProjectMapper
) : IProjectRepository {

    override suspend fun createProject(project: Project): Result<String> {
        return try {
            val entity = projectMapper.toEntity(project)
            projectDao.insert(entity)
            Result.success(project.id)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create project")
            Result.failure(e)
        }
    }

    override suspend fun updateProject(project: Project): Result<Unit> {
        return try {
            val entity = projectMapper.toEntity(project)
            projectDao.update(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update project")
            Result.failure(e)
        }
    }

    override suspend fun deleteProject(projectId: String): Result<Unit> {
        return try {
            projectDao.deleteById(projectId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete project")
            Result.failure(e)
        }
    }

    override fun getProjectById(projectId: String): Flow<Project?> {
        return projectDao.getById(projectId).map { it?.let { projectMapper.toDomain(it) } }
    }

    override fun getProjectWithSummary(projectId: String): Flow<ProjectWithSummary?> {
        return combine(
            projectDao.getById(projectId),
            expenseDao.getProjectSummary(projectId)
        ) { projectEntity, summary ->
            projectEntity?.let {
                ProjectWithSummary(
                    project = projectMapper.toDomain(it),
                    totalExpenses = summary.totalExpenses ?: 0.0,
                    budgetUtilization = if (it.totalBudget > 0) {
                        ((summary.totalExpenses ?: 0.0) / it.totalBudget) * 100
                    } else 0.0,
                    expenseCount = summary.expenseCount ?: 0,
                    thisMonthExpenses = summary.thisMonthExpenses ?: 0.0
                )
            }
        }
    }

    override fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAll().map { entities ->
            entities.map { projectMapper.toDomain(it) }
        }
    }

    override fun getActiveProjects(): Flow<List<Project>> {
        return projectDao.getActive().map { entities ->
            entities.map { projectMapper.toDomain(it) }
        }
    }
}
