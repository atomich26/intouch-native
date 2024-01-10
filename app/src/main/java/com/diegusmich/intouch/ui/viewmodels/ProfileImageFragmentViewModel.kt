package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.ktx.functions
import kotlinx.coroutines.launch

class ProfileImageFragmentViewModel : StateViewModel() {

    private val _imagePath : MutableLiveData<String?> = MutableLiveData(null)
    val imagePath : LiveData<String?> = _imagePath

    fun loadImage(path: String) {
        _imagePath.value = path
    }

    fun editImage(path: String){

    }

    fun deleteImage() = viewModelScope.launch {
        updateState(_LOADING, true)

    }
}