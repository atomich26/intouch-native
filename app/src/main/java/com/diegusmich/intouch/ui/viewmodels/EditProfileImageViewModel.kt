package com.diegusmich.intouch.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.providers.CloudImageProvider
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException

class EditProfileImageViewModel : StateViewModel() {

    private val _currentImgRef = MutableLiveData<StorageReference?>(null)
    val currentImgRef: LiveData<StorageReference?> = _currentImgRef

    private val _IMAGE_REMOVED = MutableLiveData<Boolean>(false)
    val IMAGE_REMOVED: LiveData<Boolean> = _IMAGE_REMOVED

    fun onSetCurrentImgRef(path: String?) {
        path?.let {
            _currentImgRef.value = CloudImageProvider.USERS.imageRef(path)
        }
    }

    fun onLoadImage(ctx: Context, image: File) = viewModelScope.launch {
        updateState(_LOADING, true)

        try {
            _currentImgRef.value = CloudImageProvider.USERS.uploadImage(ctx, image)
            UserRepository.changeProfileImg(_currentImgRef.value!!.name)

            updateState(_LOADING, false)
        } catch (e: Exception) {
            _currentImgRef.value = null
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }

    fun onRemoveImage() = viewModelScope.launch {
        if (currentImgRef.value == null)
            return@launch

        updateState(_LOADING, true)

        try {
            UserRepository.removeProfileImage()
            _currentImgRef.value = null

            updateState(_IMAGE_REMOVED, true)
        } catch (e: Exception) {
            val messageId =
                if (e.cause is UnknownHostException || e.cause is ConnectException)
                    R.string.firebaseNetworkException
                else
                    R.string.internal_error

            updateState(_ERROR, messageId)
        }
    }
}