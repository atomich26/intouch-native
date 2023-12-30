package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

class ProfileImageFragmentViewModel : StateViewModel() {

    private val _imagePath : MutableLiveData<String?> = MutableLiveData(null)
    val imagePath : LiveData<String?> = _imagePath

    fun loadImage(path: String) {

    }

    fun deleteImage(){

    }
}