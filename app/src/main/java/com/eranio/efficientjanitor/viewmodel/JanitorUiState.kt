package com.eranio.efficientjanitor.viewmodel

import com.eranio.efficientjanitor.data.local.BagEntity
import com.eranio.efficientjanitor.ui.result.Trip

data class JanitorUiState(
    val bags: List<BagEntity> = emptyList(),
    val trips: List<Trip> = emptyList()
)