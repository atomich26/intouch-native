package com.diegusmich.intouch.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class StateViewModel : ViewModel() {

    protected val _uiState = MutableStateFlow<UiState>(UiState.READY)
    val uiState = _uiState.asStateFlow()

    protected var _errorMessage : Int? = null
    val errorMessage get() = _errorMessage

    protected fun updateState(state : UiState){

        val prevState = uiState.value

        if(state !is UiState.LOADING && state !is UiState.READY){
            if(prevState is UiState.LOADING)
                _uiState.update { UiState.LOADING_COMPLETED }
        }

        _uiState.update { state }
    }

    fun consumeEvent(){
        if(uiState.value !is UiState.LOADING)
            _uiState.update { UiState.CONSUMED }
    }
}