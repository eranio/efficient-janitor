package com.eranio.efficientjanitor.domain

import com.eranio.efficientjanitor.data.local.BagEntity
import kotlinx.coroutines.flow.Flow

interface BagRepository {
    val bagsFlow: Flow<List<BagEntity>>
    suspend fun addBag(weight: Double)
    suspend fun deleteBag(id: Long)
    suspend fun clearBags()
}