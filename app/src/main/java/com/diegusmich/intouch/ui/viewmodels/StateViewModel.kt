package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


open class StateViewModel : ViewModel() {

    protected val _LOADING = MutableLiveData(false)
    val LOADING : LiveData<Boolean> = _LOADING

    protected val _CONTENT_LOADED = MutableLiveData(false)
    val CONTENT_LOADED : LiveData<Boolean> = _CONTENT_LOADED

    protected val _ERROR = MutableLiveData<Int?>(null)
    val ERROR : LiveData<Int?> = _ERROR


    protected fun <T> updateState(stateHandler : MutableLiveData<T>, newValue: T){
        if(stateHandler != _LOADING){
            _LOADING.value = false
        }
        stateHandler.value = newValue
    }
}