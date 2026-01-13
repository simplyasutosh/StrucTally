package com.construction.expense.domain.usecase.project

import com.construction.expense.domain.model.ProjectWithSummary
import com.construction.expense.domain.repository.IProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting project with summary data
 */
@Singleton
class GetProjectWithSummaryUseCase @Inject constructor(
    private val projectRepository: IProjectRepository
) {

    operator fun invoke(projectId: String): Flow<ProjectWithSummary?> {
        return projectRepository.getProjectWithSummary(projectId)
    }
}
