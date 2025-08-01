package com.eranio.efficientjanitor.viewmodel

import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eranio.efficientjanitor.R
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
            (random.nextDouble(MIN_BAG_WEIGHT, MAX_BAG_WEIGHT) * 100).roundToInt() / 100.0
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
            sendUiEvent(UiEvent.ShowSnackbar(R.string.cant_be_empty, emptyList()))
            return
        }

        if (weight !in MIN_BAG_WEIGHT..MAX_BAG_WEIGHT) {
            viewModelScope.launch {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.invalid_weight_range, listOf(MIN_BAG_WEIGHT, MAX_BAG_WEIGHT)))
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
