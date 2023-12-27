package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserFriendsActivityViewModel : StateViewModel() {

    private val _userFriends: MutableLiveData<List<User.Preview>?> = MutableLiveData()
    val userFriends: LiveData<List<User.Preview>?> = _userFriends

    fun onLoadFriends(userId: String?, isRefreshing: Boolean = false) = viewModelScope.launch {
        if (userId.isNullOrBlank()) {
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)
        }

        updateState(_LOADING, true)

        try {
            _userFriends.value = UserRepository.userFriends(userId)
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            val messageId =
                if (isRefreshing)
                    R.string.unable_to_update_error
                else
                    R.string.firebaseNetworkException
            updateState(_ERROR, messageId)
        }
    }
}