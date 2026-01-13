package com.construction.expense.domain.usecase.category

import com.construction.expense.domain.model.CategoryWithSubCategories
import com.construction.expense.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting categories with their subcategories
 */
@Singleton
class GetCategoriesWithSubCategoriesUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {

    operator fun invoke(): Flow<List<CategoryWithSubCategories>> {
        return categoryRepository.getCategoriesWithSubCategories()
    }
}
