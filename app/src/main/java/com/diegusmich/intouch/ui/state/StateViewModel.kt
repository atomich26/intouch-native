package com.diegusmich.intouch.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class StateViewModel : ViewModel() {

    protected val _uiEvent = MutableStateFlow<UiEvent>(UiEvent.READY)
    val uiEvent = _uiEvent.asStateFlow()

    fun consumeEvent(){
        if(uiEvent.value !is UiEvent.LOADING)
            _uiEvent.update { UiEvent.CONSUMED }
    }
}