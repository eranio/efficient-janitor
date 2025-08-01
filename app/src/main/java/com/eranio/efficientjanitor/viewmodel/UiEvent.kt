package com.eranio.efficientjanitor.viewmodel

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowSnackbar(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList()
    ) : UiEvent()
    data object ClearSuccess : UiEvent()
}