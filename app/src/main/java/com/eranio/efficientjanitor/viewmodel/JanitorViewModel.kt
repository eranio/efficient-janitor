package com.eranio.efficientjanitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eranio.efficientjanitor.domain.TripsCalculator
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
    private val tripsCalculator: TripsCalculator
): ViewModel() {

    private val _uiState = MutableStateFlow(JanitorUiState())
    val uiState: StateFlow<JanitorUiState> = _uiState

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun addRandomBags(count: Int = 10) {
        val random = Random(System.currentTimeMillis())
        val newBags = List(count) {
            (random.nextDouble(0.1, 3.0) * 100).roundToInt() / 100.0
        }

        val updated = _uiState.value.bags + newBags
        _uiState.value = _uiState.value.copy(bags = updated)
    }

    fun onAddRandomBagsClicked() {
        addRandomBags()
    }

    fun onAddBagClicked(inputText: String) {
        val weight = inputText.toDoubleOrNull()
        val current = _uiState.value ?: return
        if (weight == null) {
            sendUiEvent(UiEvent.ShowSnackbar("Can't be empty"))
            return
        }

        if (weight !in 1.01..3.0) {
            viewModelScope.launch {
                sendUiEvent(UiEvent.ShowSnackbar("Invalid weight (1.01 - 3.0 Kg)"))
            }
            return
        }
        _uiState.value = current.copy(bags = current.bags + weight)
    }

    fun onClearBagsClicked() {
        _uiState.value = JanitorUiState()
        viewModelScope.launch {
            sendUiEvent(UiEvent.ClearSuccess)
        }
    }

    fun onRemoveBag(weight: Double) {
        val current = _uiState.value ?: return
        _uiState.value = current.copy(bags = current.bags - weight)
    }

    fun onCalculateClicked() {
        val current = _uiState.value ?: return
        val trips = tripsCalculator.calculateTrips(current.bags)
        _uiState.value = current.copy(trips = trips)
    }
}
