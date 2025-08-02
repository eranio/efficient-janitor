package com.eranio.efficientjanitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.domain.BagRepository
import com.eranio.efficientjanitor.domain.TripsCalculator
import com.eranio.efficientjanitor.util.Constants.MAX_BAG_WEIGHT
import com.eranio.efficientjanitor.util.Constants.MIN_BAG_WEIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

@HiltViewModel
class JanitorViewModel @Inject constructor(
    private val bagRepository: BagRepository,
    private val tripsCalculator: TripsCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(JanitorUiState())
    val uiState: StateFlow<JanitorUiState> = _uiState

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observeBags()
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun observeBags() {
        viewModelScope.launch {
            bagRepository.bagsFlow.collect { bagList ->
                _uiState.value = _uiState.value.copy(bags = bagList)
            }
        }
    }

    fun onAddRandomBagsClicked() {
        val random = Random(System.currentTimeMillis())
        viewModelScope.launch {
            repeat(10) {
                val weight = (random.nextDouble(MIN_BAG_WEIGHT, MAX_BAG_WEIGHT) * 100).roundToInt() / 100.0
                bagRepository.addBag(weight)
            }
        }
    }

    fun onAddBagClicked(inputText: String) {
        val weight = inputText.toDoubleOrNull()
        if (weight == null) {
            sendUiEvent(UiEvent.ShowSnackbar(R.string.cant_be_empty, emptyList()))
            return
        }

        if (weight !in MIN_BAG_WEIGHT..MAX_BAG_WEIGHT) {
            sendUiEvent(UiEvent.ShowSnackbar(R.string.invalid_weight_range, listOf(MIN_BAG_WEIGHT, MAX_BAG_WEIGHT)))
            return
        }

        viewModelScope.launch {
            bagRepository.addBag(weight)
        }
    }

    fun onClearBagsClicked() {
        viewModelScope.launch {
            bagRepository.clearBags()
            sendUiEvent(UiEvent.ClearSuccess)
        }
    }

    fun onRemoveBag(weight: Double) {
        val current = _uiState.value ?: return
        _uiState.value = current.copy(bags = current.bags - weight)
        viewModelScope.launch {
            bagRepository.deleteBag(weight)
        }
    }

    fun onCalculateClicked() {
        val currentBags = _uiState.value.bags
        val trips = tripsCalculator.calculateTrips(currentBags)
        _uiState.value = _uiState.value.copy(trips = trips)
    }
}
