package com.eranio.efficientjanitor.viewmodel

data class JanitorUiState(
    val bags: List<Double> = emptyList(),
    val trips: List<List<Double>> = emptyList()
)