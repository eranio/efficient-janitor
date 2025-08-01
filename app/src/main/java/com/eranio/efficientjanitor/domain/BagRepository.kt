package com.eranio.efficientjanitor.domain

import kotlinx.coroutines.flow.Flow

interface BagRepository {
    val bagsFlow: Flow<List<Double>>
    suspend fun addBag(weight: Double)
    suspend fun deleteBag(weight: Double)
    suspend fun clearBags()
}