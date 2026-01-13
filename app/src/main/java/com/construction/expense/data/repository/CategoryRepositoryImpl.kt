package com.construction.expense.data.repository

import com.construction.expense.data.local.dao.CategoryDao
import com.construction.expense.data.local.dao.SubCategoryDao
import com.construction.expense.data.local.mappers.CategoryMapper
import com.construction.expense.data.local.mappers.SubCategoryMapper
import com.construction.expense.domain.model.Category
import com.construction.expense.domain.model.CategoryWithSubCategories
import com.construction.expense.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val subCategoryDao: SubCategoryDao,
    private val categoryMapper: CategoryMapper,
    private val subCategoryMapper: SubCategoryMapper
) : ICategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll().map { entities ->
            entities.map { categoryMapper.toDomain(it) }
        }
    }

    override fun getCategoryById(categoryId: Int): Flow<Category?> {
        return categoryDao.getById(categoryId).map { it?.let { categoryMapper.toDomain(it) } }
    }

    override fun getCategoriesWithSubCategories(): Flow<List<CategoryWithSubCategories>> {
        return combine(
            categoryDao.getAll(),
            subCategoryDao.getAll()
        ) { categories, subCategories ->
            categories.map { category ->
                val subs = subCategories
                    .filter { it.categoryId == category.id }
                    .map { subCategoryMapper.toDomain(it) }
                CategoryWithSubCategories(
                    category = categoryMapper.toDomain(category),
                    subCategories = subs
                )
            }
        }
    }
}
