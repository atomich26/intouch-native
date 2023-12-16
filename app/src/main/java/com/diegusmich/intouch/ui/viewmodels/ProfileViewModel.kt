package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.UserProfile
import com.diegusmich.intouch.data.repository.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProfileViewModel : StateViewModel() {

    private val _profile: MutableLiveData<UserProfile?> = MutableLiveData(null)
    val profile: LiveData<UserProfile?> = _profile

    fun onLogout() = Firebase.auth.signOut()

    fun onLoadProfile(userId: String?) = viewModelScope.launch {

        if (userId == null)
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)

        updateState(_LOADING, true)

        try {
            _profile.value = UserRepository.userProfile(userId)
            updateState(_CONTENT_LOADED, true)
        } catch (e: FirebaseFirestoreException) {
            updateState(_ERROR, R.string.unable_to_update_error)
        }
    }

    fun onLoadAuthProfile() = viewModelScope.launch {
        onLoadProfile(Firebase.auth.currentUser?.uid!!)
    }
}