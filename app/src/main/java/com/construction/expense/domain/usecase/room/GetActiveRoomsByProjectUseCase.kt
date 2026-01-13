package com.construction.expense.domain.usecase.room

import com.construction.expense.domain.model.RoomModel
import com.construction.expense.domain.repository.IRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting active rooms for a project
 */
@Singleton
class GetActiveRoomsByProjectUseCase @Inject constructor(
    private val roomRepository: IRoomRepository
) {

    operator fun invoke(projectId: String): Flow<List<RoomModel>> {
        return roomRepository.getActiveRoomsByProject(projectId)
    }
}
