package com.eranio.efficientjanitor.data

import com.eranio.efficientjanitor.data.local.BagDao
import com.eranio.efficientjanitor.data.local.BagEntity
import com.eranio.efficientjanitor.domain.BagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomBagRepository @Inject constructor(
    private val dao: BagDao
) : BagRepository {

    override val bagsFlow: Flow<List<Double>> =
        dao.getAllBags().map { list: List<BagEntity> -> list.map { it.weight } }


    override suspend fun addBag(weight: Double) {
        dao.insertBag(BagEntity(weight = weight))
    }

    override suspend fun deleteBag(weight: Double) {
        dao.deleteBag(BagEntity(weight = weight))
    }

    override suspend fun clearBags() {
        dao.clearAllBags()
    }
}
