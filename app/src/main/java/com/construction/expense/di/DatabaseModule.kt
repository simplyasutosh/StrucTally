package com.construction.expense.di

import android.content.Context
import androidx.room.Room
import com.construction.expense.data.local.ConstructionExpenseDatabase
import com.construction.expense.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ConstructionExpenseDatabase {
        return Room.databaseBuilder(
            context,
            ConstructionExpenseDatabase::class.java,
            ConstructionExpenseDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: ConstructionExpenseDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: ConstructionExpenseDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideSubCategoryDao(database: ConstructionExpenseDatabase): SubCategoryDao {
        return database.subCategoryDao()
    }

    @Provides
    @Singleton
    fun provideCategoryBudgetDao(database: ConstructionExpenseDatabase): CategoryBudgetDao {
        return database.categoryBudgetDao()
    }

    @Provides
    @Singleton
    fun provideRoomDao(database: ConstructionExpenseDatabase): RoomDao {
        return database.roomDao()
    }

    @Provides
    @Singleton
    fun provideMilestoneDao(database: ConstructionExpenseDatabase): MilestoneDao {
        return database.milestoneDao()
    }

    @Provides
    @Singleton
    fun provideVendorDao(database: ConstructionExpenseDatabase): VendorDao {
        return database.vendorDao()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: ConstructionExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }
}
