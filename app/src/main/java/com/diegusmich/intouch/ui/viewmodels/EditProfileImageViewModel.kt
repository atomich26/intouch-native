package com.diegusmich.intouch.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.providers.CloudImageProvider
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch

class EditProfileImageViewModel : StateViewModel() {

    private val _currentImgRef = MutableLiveData<StorageReference?>(null)
    val currentImgRef: LiveData<StorageReference?> = _currentImgRef

    private val _IMAGE_REMOVED = MutableLiveData<Boolean>(false)
    val IMAGE_REMOVED : LiveData<Boolean> = _IMAGE_REMOVED

    fun onSetCurrentImgRef(path: String?) {
        path?.let {
            _currentImgRef.value = CloudImageProvider.USERS.imageRef(path)
        }
    }

    fun onLoadImage(imageUri: Uri) = viewModelScope.launch {
        updateState(_LOADING, true)

        try {
            _currentImgRef.value = CloudImageProvider.USERS.uploadImage(imageUri)
            UserRepository.changeProfileImg()

            updateState(_LOADING, false)
        } catch (e: Exception) {
            Log.d("UPLOAD", e.message.toString())
            updateState(_ERROR, R.string.firebaseNetworkException)
        }

    }

    fun onRemoveImage() = viewModelScope.launch {

        updateState(_LOADING, true)

        try {
            updateState(_IMAGE_REMOVED, true)
        } catch (e: Exception) {

        }
    }
}