package com.construction.expense.domain.usecase.report

import com.construction.expense.domain.model.VendorExpenseSummary
import com.construction.expense.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generate vendor-wise expense summary and analysis
 *
 * Provides for each vendor:
 * - Vendor name and details
 * - Total amount paid
 * - Number of transactions
 * - Average transaction amount
 * - Categories they work in
 * - Payment mode preferences
 * - Last transaction date
 * - Outstanding payments (if applicable)
 */
@Singleton
class GenerateVendorSummaryUseCase @Inject constructor(
    private val reportRepository: IReportRepository
) {

    /**
     * Generate vendor-wise summary for project
     *
     * @param projectId Project ID
     * @return Flow of list of VendorExpenseSummary, sorted by total amount (descending)
     */
    operator fun invoke(projectId: String): Flow<List<VendorExpenseSummary>> {
        return reportRepository.getVendorExpenseSummary(projectId)
    }

    /**
     * Generate summary for specific vendor
     *
     * @param projectId Project ID
     * @param vendorName Vendor name
     * @return Flow of VendorExpenseSummary for the specific vendor
     */
    fun forVendor(projectId: String, vendorName: String): Flow<VendorExpenseSummary?> {
        return reportRepository.getVendorExpenseSummaryByName(projectId, vendorName)
    }

    /**
     * Get top vendors by spending
     *
     * @param projectId Project ID
     * @param limit Number of top vendors to return (default: 10)
     * @return Flow of list of top VendorExpenseSummary
     */
    fun getTopVendors(projectId: String, limit: Int = 10): Flow<List<VendorExpenseSummary>> {
        return invoke(projectId).map { vendors ->
            vendors.take(limit)
        }
    }

    /**
     * Analyze vendor diversity
     *
     * Checks if spending is concentrated with few vendors or distributed
     *
     * @param projectId Project ID
     * @return Flow of VendorDiversityAnalysis
     */
    fun analyzeDiversity(projectId: String): Flow<VendorDiversityAnalysis> {
        return invoke(projectId).map { vendors ->
            calculateDiversity(vendors)
        }
    }

    /**
     * Calculate vendor diversity metrics
     */
    private fun calculateDiversity(vendors: List<VendorExpenseSummary>): VendorDiversityAnalysis {
        if (vendors.isEmpty()) {
            return VendorDiversityAnalysis(
                totalVendors = 0,
                totalAmount = 0.0,
                top5Percentage = 0.0,
                top10Percentage = 0.0,
                concentrationLevel = ConcentrationLevel.LOW
            )
        }

        val totalAmount = vendors.sumOf { it.totalAmount }
        val top5Amount = vendors.take(5).sumOf { it.totalAmount }
        val top10Amount = vendors.take(10).sumOf { it.totalAmount }

        val top5Percentage = if (totalAmount > 0) (top5Amount / totalAmount) * 100 else 0.0
        val top10Percentage = if (totalAmount > 0) (top10Amount / totalAmount) * 100 else 0.0

        val concentrationLevel = when {
            top5Percentage > 80 -> ConcentrationLevel.HIGH  // 5 vendors = 80%+ spending
            top5Percentage > 60 -> ConcentrationLevel.MEDIUM // 5 vendors = 60-80% spending
            else -> ConcentrationLevel.LOW // Spending is distributed
        }

        return VendorDiversityAnalysis(
            totalVendors = vendors.size,
            totalAmount = totalAmount,
            top5Percentage = top5Percentage,
            top10Percentage = top10Percentage,
            concentrationLevel = concentrationLevel
        )
    }

    /**
     * Get vendor payment analysis
     *
     * Analyzes payment patterns for vendors
     *
     * @param projectId Project ID
     * @return Flow of list of VendorPaymentPattern
     */
    fun getPaymentPatterns(projectId: String): Flow<List<VendorPaymentPattern>> {
        return invoke(projectId).map { vendors ->
            vendors.map { vendor ->
                VendorPaymentPattern(
                    vendorName = vendor.vendorName,
                    totalAmount = vendor.totalAmount,
                    transactionCount = vendor.transactionCount,
                    averageAmount = vendor.averageAmount,
                    preferredPaymentMode = vendor.preferredPaymentMode,
                    lastTransactionDate = vendor.lastTransactionDate
                )
            }
        }
    }
}

/**
 * Vendor diversity analysis
 */
data class VendorDiversityAnalysis(
    val totalVendors: Int,
    val totalAmount: Double,
    val top5Percentage: Double,  // % of spending with top 5 vendors
    val top10Percentage: Double, // % of spending with top 10 vendors
    val concentrationLevel: ConcentrationLevel
)

/**
 * Spending concentration level
 */
enum class ConcentrationLevel {
    HIGH,    // Spending concentrated with few vendors (risky)
    MEDIUM,  // Moderate concentration
    LOW      // Well distributed (healthy)
}

/**
 * Vendor payment pattern
 */
data class VendorPaymentPattern(
    val vendorName: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val averageAmount: Double,
    val preferredPaymentMode: String?,
    val lastTransactionDate: Long?
)
