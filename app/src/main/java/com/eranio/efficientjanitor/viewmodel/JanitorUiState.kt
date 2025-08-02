package com.eranio.efficientjanitor.viewmodel

import com.eranio.efficientjanitor.data.local.BagEntity

data class JanitorUiState(
    val bags: List<BagEntity> = emptyList(),
    val trips: List<List<Double>> = emptyList()
)