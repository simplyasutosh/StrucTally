package com.construction.expense.domain.repository

import com.construction.expense.domain.model.Project
import com.construction.expense.domain.model.ProjectWithSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Project operations
 */
interface IProjectRepository {

    /**
     * Create new project
     */
    suspend fun createProject(project: Project): Result<String>

    /**
     * Update existing project
     */
    suspend fun updateProject(project: Project): Result<Unit>

    /**
     * Delete project
     */
    suspend fun deleteProject(projectId: String): Result<Unit>

    /**
     * Get project by ID
     */
    fun getProjectById(projectId: String): Flow<Project?>

    /**
     * Get project with summary data
     */
    fun getProjectWithSummary(projectId: String): Flow<ProjectWithSummary?>

    /**
     * Get all projects
     */
    fun getAllProjects(): Flow<List<Project>>

    /**
     * Get active projects only
     */
    fun getActiveProjects(): Flow<List<Project>>
}
