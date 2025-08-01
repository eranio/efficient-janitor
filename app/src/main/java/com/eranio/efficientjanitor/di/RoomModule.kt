package com.eranio.efficientjanitor.di

import android.content.Context
import androidx.room.Room
import com.eranio.efficientjanitor.data.local.AppDatabase
import com.eranio.efficientjanitor.data.local.BagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bags.db"
        ).build()
    }

    @Provides
    fun provideBagDao(db: AppDatabase): BagDao = db.bagDao()
}
