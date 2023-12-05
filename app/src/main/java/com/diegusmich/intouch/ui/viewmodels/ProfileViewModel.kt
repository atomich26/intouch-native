package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.UserProfile
import com.diegusmich.intouch.data.repository.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class ProfileViewModel : StateViewModel() {

    private val _profile: MutableLiveData<UserProfile?> = MutableLiveData(null)
    val profile : LiveData<UserProfile?> = _profile

    fun onLogout() = Firebase.auth.signOut()

    fun loadProfile(userId: String?) = viewModelScope.launch {

        if(userId == null){
            updateState(_ERROR, R.string.firebaseAuthInvalidUserException)
            return@launch
        }

        updateState(_LOADING, true)

        try{
            _profile.value = UserRepository.getUserProfile(userId)
            updateState(_CONTENT_LOADED, true)
        }catch (e : Exception){
            val messageId =
                if (e.cause is UnknownHostException || e.cause is ConnectException)
                    R.string.firebaseNetworkException
                else
                    R.string.firebaseDefaultExceptionMessage

            updateState(_ERROR, messageId)
        }
    }

    fun loadAuthProfile() = viewModelScope.launch {
        loadProfile(Firebase.auth.currentUser?.uid!!)
    }
}