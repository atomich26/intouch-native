package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class UserFriendsActivityViewModel : StateViewModel() {

    private val _userFriends : MutableLiveData<List<UserPreview>?> = MutableLiveData()
    val userFriends : LiveData<List<UserPreview>?> = _userFriends

    fun onLoadFriends(userId: String?) = viewModelScope.launch {
        if(userId.isNullOrBlank()){
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)
        }

        updateState(_LOADING, true)

        try{
            _userFriends.value = UserRepository.userFriends(userId)
            updateState(_CONTENT_LOADED, true)

        }catch(e : Exception){
            val messageId = if (e.cause is UnknownHostException || e.cause is ConnectException)
                R.string.firebaseNetworkException
            else
                R.string.firebaseDefaultExceptionMessage

            updateState(_ERROR, messageId)
        }
    }
}