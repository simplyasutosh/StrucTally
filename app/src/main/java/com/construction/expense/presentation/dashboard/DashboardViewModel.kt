package com.construction.expense.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.construction.expense.domain.model.*
import com.construction.expense.domain.usecase.expense.GetExpensesByProjectUseCase
import com.construction.expense.domain.usecase.project.GetAllProjectsUseCase
import com.construction.expense.domain.usecase.project.GetProjectWithSummaryUseCase
import com.construction.expense.domain.usecase.report.GenerateCategorySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Dashboard screen
 *
 * Manages:
 * - Project selection
 * - Project summary data
 * - Recent expenses
 * - Category breakdown
 * - Budget status
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getProjectWithSummaryUseCase: GetProjectWithSummaryUseCase,
    private val getExpensesByProjectUseCase: GetExpensesByProjectUseCase,
    private val generateCategorySummaryUseCase: GenerateCategorySummaryUseCase
) : ViewModel() {

    // ===== STATE =====

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Selected project ID
    private val _selectedProjectId = MutableStateFlow<String?>(null)

    init {
        loadProjects()
    }

    // ===== ACTIONS =====

    /**
     * Load all projects
     */
    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getAllProjectsUseCase()
                .catch { e ->
                    Timber.e(e, "Failed to load projects")
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to load projects: ${e.message}"
                    )}
                }
                .collect { projects ->
                    _uiState.update { it.copy(
                        projects = projects,
                        isLoading = false
                    )}

                    // Auto-select first active project
                    if (_selectedProjectId.value == null && projects.isNotEmpty()) {
                        val firstActive = projects.firstOrNull {
                            it.status == ProjectStatus.ACTIVE
                        } ?: projects.first()
                        selectProject(firstActive.id)
                    }
                }
        }
    }

    /**
     * Select a project
     */
    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
        loadProjectData(projectId)
    }

    /**
     * Load all data for selected project
     */
    private fun loadProjectData(projectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Combine multiple flows
            combine(
                getProjectWithSummaryUseCase(projectId),
                getExpensesByProjectUseCase.getRecent(projectId, 10),
                generateCategorySummaryUseCase(projectId)
            ) { projectSummary, recentExpenses, categorySummary ->
                Triple(projectSummary, recentExpenses, categorySummary)
            }
            .catch { e ->
                Timber.e(e, "Failed to load project data")
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load data: ${e.message}"
                )}
            }
            .collect { (projectSummary, recentExpenses, categorySummary) ->
                projectSummary?.let { summary ->
                    _uiState.update { state ->
                        state.copy(
                            selectedProject = summary.project,
                            totalSpent = summary.totalExpenses,
                            budgetRemaining = summary.project.budgetRemaining,
                            budgetUtilization = summary.budgetUtilization,
                            expenseCount = summary.expenseCount,
                            thisMonthExpenses = summary.thisMonthExpenses,
                            recentExpenses = recentExpenses,
                            categoryBreakdown = categorySummary,
                            budgetStatus = when {
                                summary.project.isOverBudget -> BudgetStatus.OVER_BUDGET
                                summary.budgetUtilization >= 95 -> BudgetStatus.AT_BUDGET
                                else -> BudgetStatus.UNDER_BUDGET
                            },
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    /**
     * Refresh dashboard data
     */
    fun refresh() {
        _selectedProjectId.value?.let { projectId ->
            loadProjectData(projectId)
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * Dashboard UI state
 */
data class DashboardUiState(
    val selectedProject: Project? = null,
    val projects: List<Project> = emptyList(),
    val totalSpent: Double = 0.0,
    val budgetRemaining: Double = 0.0,
    val budgetUtilization: Double = 0.0,
    val expenseCount: Int = 0,
    val thisMonthExpenses: Double = 0.0,
    val recentExpenses: List<Expense> = emptyList(),
    val categoryBreakdown: List<CategoryExpenseSummary> = emptyList(),
    val budgetStatus: BudgetStatus = BudgetStatus.UNDER_BUDGET,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Budget status indicator
 */
enum class BudgetStatus {
    UNDER_BUDGET,  // < 95% utilized
    AT_BUDGET,     // 95-100% utilized
    OVER_BUDGET    // > 100% utilized
}
