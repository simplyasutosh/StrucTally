package com.construction.expense.domain.usecase.report

import com.construction.expense.domain.model.RoomExpenseSummary
import com.construction.expense.domain.repository.IReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generate room-wise expense summary
 *
 * Provides for each room:
 * - Room name and ID
 * - Total spent
 * - Number of expenses
 * - Category breakdown (which categories spent in this room)
 * - Average expense amount
 * - Most expensive item
 * - Completion percentage (if applicable)
 */
@Singleton
class GenerateRoomSummaryUseCase @Inject constructor(
    private val reportRepository: IReportRepository
) {

    /**
     * Generate room-wise summary for project
     *
     * @param projectId Project ID
     * @return Flow of list of RoomExpenseSummary, sorted by total spent (descending)
     */
    operator fun invoke(projectId: String): Flow<List<RoomExpenseSummary>> {
        return reportRepository.getRoomExpenseSummary(projectId)
    }

    /**
     * Generate summary for specific room
     *
     * @param projectId Project ID
     * @param roomId Room ID
     * @return Flow of RoomExpenseSummary for the specific room
     */
    fun forRoom(projectId: String, roomId: Int): Flow<RoomExpenseSummary?> {
        return reportRepository.getRoomExpenseSummaryById(projectId, roomId)
    }

    /**
     * Compare room expenses
     *
     * Useful for understanding which rooms are most/least expensive
     *
     * @param projectId Project ID
     * @return Flow of list of RoomExpenseSummary with comparison metrics
     */
    fun compareRooms(projectId: String): Flow<List<RoomExpenseSummary>> {
        return invoke(projectId)
    }
}
