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
        // Calculate start of current month
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val currentMonthStart = calendar.timeInMillis
        
        return combine(
            projectDao.getById(projectId),
            expenseDao.getProjectSummary(projectId, currentMonthStart)
        ) { projectEntity, summary ->
            try {
                projectEntity?.let {
                    ProjectWithSummary(
                        project = projectMapper.toDomain(it),
                        totalExpenses = summary.totalExpenses,
                        budgetUtilization = if (it.totalBudget > 0) {
                            (summary.totalExpenses / it.totalBudget) * 100
                        } else 0.0,
                        expenseCount = summary.expenseCount,
                        thisMonthExpenses = summary.thisMonthExpenses
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error creating ProjectWithSummary for projectId: $projectId")
                null
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
