package com.construction.expense.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.construction.expense.domain.model.*
import com.construction.expense.domain.usecase.report.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Reports screen
 *
 * Manages:
 * - Multiple report types
 * - Chart data preparation
 * - Export functionality
 * - Report filtering
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val generateProjectSummaryUseCase: GenerateProjectSummaryUseCase,
    private val generateCategorySummaryUseCase: GenerateCategorySummaryUseCase,
    private val generateRoomSummaryUseCase: GenerateRoomSummaryUseCase,
    private val generateMonthlySummaryUseCase: GenerateMonthlySummaryUseCase,
    private val generateVendorSummaryUseCase: GenerateVendorSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    /**
     * Load report for given project and type
     */
    fun loadReport(projectId: String, reportType: ReportType) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true,
                currentReportType = reportType,
                error = null
            )}

            when (reportType) {
                ReportType.PROJECT_SUMMARY -> loadProjectSummary(projectId)
                ReportType.CATEGORY_BREAKDOWN -> loadCategoryBreakdown(projectId)
                ReportType.ROOM_SUMMARY -> loadRoomSummary(projectId)
                ReportType.MONTHLY_TREND -> loadMonthlyTrend(projectId)
                ReportType.VENDOR_ANALYSIS -> loadVendorAnalysis(projectId)
            }
        }
    }

    /**
     * Load project summary report
     */
    private fun loadProjectSummary(projectId: String) {
        viewModelScope.launch {
            generateProjectSummaryUseCase(projectId)
                .catch { e ->
                    Timber.e(e, "Failed to load project summary")
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to load project summary: ${e.message}"
                    )}
                }
                .collect { summary ->
                    _uiState.update { it.copy(
                        projectSummary = summary,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }

    /**
     * Load category breakdown report
     */
    private fun loadCategoryBreakdown(projectId: String) {
        viewModelScope.launch {
            generateCategorySummaryUseCase(projectId)
                .catch { e ->
                    Timber.e(e, "Failed to load category breakdown")
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to load category breakdown: ${e.message}"
                    )}
                }
                .collect { categories ->
                    // Prepare chart data
                    val chartData = categories.associate {
                        it.categoryName to it.totalSpent
                    }

                    _uiState.update { it.copy(
                        categoryBreakdown = categories,
                        categoryChartData = chartData,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }

    /**
     * Load room summary report
     */
    private fun loadRoomSummary(projectId: String) {
        viewModelScope.launch {
            generateRoomSummaryUseCase(projectId)
                .catch { e ->
                    Timber.e(e, "Failed to load room summary")
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to load room summary: ${e.message}"
                    )}
                }
                .collect { rooms ->
                    // Prepare chart data
                    val chartData = rooms.associate {
                        it.roomName to it.totalSpent
                    }

                    _uiState.update { it.copy(
                        roomSummary = rooms,
                        roomChartData = chartData,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }

    /**
     * Load monthly trend report
     */
    private fun loadMonthlyTrend(projectId: String, months: Int = 12) {
        viewModelScope.launch {
            combine(
                generateMonthlySummaryUseCase(projectId, months),
                generateMonthlySummaryUseCase.getTrend(projectId, 6)
            ) { monthlyData, trend ->
                Pair(monthlyData, trend)
            }
            .catch { e ->
                Timber.e(e, "Failed to load monthly trend")
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load monthly trend: ${e.message}"
                )}
            }
            .collect { (monthlyData, trend) ->
                // Prepare chart data
                val chartData = monthlyData.associate {
                    "${it.month}/${it.year}" to it.totalAmount
                }

                _uiState.update { it.copy(
                    monthlyTrend = monthlyData,
                    monthlyChartData = chartData,
                    spendingTrend = trend,
                    isLoading = false,
                    error = null
                )}
            }
        }
    }

    /**
     * Load vendor analysis report
     */
    private fun loadVendorAnalysis(projectId: String) {
        viewModelScope.launch {
            combine(
                generateVendorSummaryUseCase(projectId),
                generateVendorSummaryUseCase.analyzeDiversity(projectId)
            ) { vendors, diversity ->
                Pair(vendors, diversity)
            }
            .catch { e ->
                Timber.e(e, "Failed to load vendor analysis")
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load vendor analysis: ${e.message}"
                )}
            }
            .collect { (vendors, diversity) ->
                // Prepare chart data for top 10 vendors
                val chartData = vendors.take(10).associate {
                    it.vendorName to it.totalAmount
                }

                _uiState.update { it.copy(
                    vendorAnalysis = vendors,
                    vendorChartData = chartData,
                    vendorDiversity = diversity,
                    isLoading = false,
                    error = null
                )}
            }
        }
    }

    /**
     * Get top vendors
     */
    fun getTopVendors(projectId: String, limit: Int = 10) {
        viewModelScope.launch {
            generateVendorSummaryUseCase.getTopVendors(projectId, limit)
                .catch { e ->
                    Timber.e(e, "Failed to load top vendors")
                }
                .collect { vendors ->
                    _uiState.update { it.copy(
                        topVendors = vendors
                    )}
                }
        }
    }

    /**
     * Compare rooms
     */
    fun compareRooms(projectId: String) {
        viewModelScope.launch {
            generateRoomSummaryUseCase.compareRooms(projectId)
                .catch { e ->
                    Timber.e(e, "Failed to compare rooms")
                }
                .collect { rooms ->
                    _uiState.update { it.copy(
                        roomSummary = rooms
                    )}
                }
        }
    }

    /**
     * Get month over month comparison
     */
    fun getMonthOverMonthComparison(projectId: String) {
        viewModelScope.launch {
            generateMonthlySummaryUseCase.getMonthOverMonthComparison(projectId)
                .catch { e ->
                    Timber.e(e, "Failed to get month over month comparison")
                }
                .collect { comparisons ->
                    _uiState.update { it.copy(
                        monthlyComparison = comparisons
                    )}
                }
        }
    }

    /**
     * Export report as PDF
     */
    fun exportAsPdf(projectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }

            try {
                // TODO: Implement PDF export
                Timber.d("Exporting report as PDF for project: $projectId")

                _uiState.update { it.copy(
                    isExporting = false,
                    exportSuccess = true
                )}
            } catch (e: Exception) {
                Timber.e(e, "Failed to export PDF")
                _uiState.update { it.copy(
                    isExporting = false,
                    error = "Failed to export: ${e.message}"
                )}
            }
        }
    }

    /**
     * Export report as Excel
     */
    fun exportAsExcel(projectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }

            try {
                // TODO: Implement Excel export
                Timber.d("Exporting report as Excel for project: $projectId")

                _uiState.update { it.copy(
                    isExporting = false,
                    exportSuccess = true
                )}
            } catch (e: Exception) {
                Timber.e(e, "Failed to export Excel")
                _uiState.update { it.copy(
                    isExporting = false,
                    error = "Failed to export: ${e.message}"
                )}
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Clear export success flag
     */
    fun clearExportSuccess() {
        _uiState.update { it.copy(exportSuccess = false) }
    }

    /**
     * Refresh current report
     */
    fun refresh(projectId: String) {
        _uiState.value.currentReportType?.let { reportType ->
            loadReport(projectId, reportType)
        }
    }
}

/**
 * Reports UI state
 */
data class ReportsUiState(
    val currentReportType: ReportType? = null,

    // Project summary
    val projectSummary: ExpenseSummary? = null,

    // Category breakdown
    val categoryBreakdown: List<CategoryExpenseSummary> = emptyList(),
    val categoryChartData: Map<String, Double> = emptyMap(),

    // Room summary
    val roomSummary: List<RoomExpenseSummary> = emptyList(),
    val roomChartData: Map<String, Double> = emptyMap(),

    // Monthly trend
    val monthlyTrend: List<MonthlyExpense> = emptyList(),
    val monthlyChartData: Map<String, Double> = emptyMap(),
    val monthlyComparison: List<MonthlyComparison> = emptyList(),
    val spendingTrend: SpendingTrend? = null,

    // Vendor analysis
    val vendorAnalysis: List<VendorExpenseSummary> = emptyList(),
    val vendorChartData: Map<String, Double> = emptyMap(),
    val vendorDiversity: VendorDiversityAnalysis? = null,
    val topVendors: List<VendorExpenseSummary> = emptyList(),

    // UI state
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Report type enumeration
 */
enum class ReportType {
    PROJECT_SUMMARY,      // Overall project statistics
    CATEGORY_BREAKDOWN,   // Spending by category with pie chart
    ROOM_SUMMARY,         // Spending by room/area
    MONTHLY_TREND,        // Month-over-month spending trends
    VENDOR_ANALYSIS       // Vendor spending patterns and diversity
}
