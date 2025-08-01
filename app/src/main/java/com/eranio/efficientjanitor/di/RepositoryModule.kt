package com.eranio.efficientjanitor.di

import com.eranio.efficientjanitor.data.RoomBagRepository
import com.eranio.efficientjanitor.data.local.BagDao
import com.eranio.efficientjanitor.domain.BagRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBagRepository(
        dao: BagDao
    ): BagRepository = RoomBagRepository(dao)
}
