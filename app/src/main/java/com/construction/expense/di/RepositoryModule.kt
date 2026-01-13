package com.construction.expense.di

import com.construction.expense.data.repository.*
import com.construction.expense.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): IExpenseRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        impl: ProjectRepositoryImpl
    ): IProjectRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): ICategoryRepository

    @Binds
    @Singleton
    abstract fun bindCategoryBudgetRepository(
        impl: CategoryBudgetRepositoryImpl
    ): ICategoryBudgetRepository

    @Binds
    @Singleton
    abstract fun bindRoomRepository(
        impl: RoomRepositoryImpl
    ): IRoomRepository

    @Binds
    @Singleton
    abstract fun bindVendorRepository(
        impl: VendorRepositoryImpl
    ): IVendorRepository

    @Binds
    @Singleton
    abstract fun bindMilestoneRepository(
        impl: MilestoneRepositoryImpl
    ): IMilestoneRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): IReportRepository
}
