package com.construction.expense.domain.usecase.project

import com.construction.expense.domain.model.Project
import com.construction.expense.domain.repository.IProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting all projects
 */
@Singleton
class GetAllProjectsUseCase @Inject constructor(
    private val projectRepository: IProjectRepository
) {

    operator fun invoke(): Flow<List<Project>> {
        return projectRepository.getAllProjects()
    }

    /**
     * Get active projects only
     */
    fun getActive(): Flow<List<Project>> {
        return projectRepository.getActiveProjects()
    }
}
