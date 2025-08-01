package com.eranio.efficientjanitor.viewmodel

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data object ClearSuccess : UiEvent()
}